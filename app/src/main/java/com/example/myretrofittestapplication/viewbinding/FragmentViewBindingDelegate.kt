package com.example.myretrofittestapplication.viewbinding

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// See: https://medium.com/flobiz-blog/fragment-view-binding-initialisation-using-delegates-8cd50b41e1d2
//      https://zhuinden.medium.com/simple-one-liner-viewbinding-in-fragments-and-activities-with-kotlin-961430c6c07c


/**
 * Usage:
 * private val binding: AnyFragmentBinding by viewBinding() for AnyFragmentBinding : Fragment(R.layout.any_fragment)
 */
inline fun <reified T : ViewBinding> Fragment.viewBinding() =
    FragmentViewBindingDelegate(T::class.java, this)


class FragmentViewBindingDelegate<T : ViewBinding>(
    private val bindingClass: Class<T>,
    private val fragment: Fragment,
) : ReadOnlyProperty<Fragment, T> {

    private var binding: T? = null
    private val bindingMethod = bindingClass.getMethod("bind", View::class.java)


    override fun getValue(thisRef: Fragment, property: KProperty<*>): T =
        binding ?: bind(thisRef).also {
            binding = it
        }


    @Suppress("UNCHECKED_CAST")
    private fun bind(thisRef: Fragment): T {
        addLifecycleObserver()
        verifyLifecycleState()
        return bindingMethod.invoke(null, thisRef.requireView()) as T
    }

    private fun addLifecycleObserver() {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val viewLifecycleOwnerLiveDataObserver =
                Observer<LifecycleOwner?> {
                    val viewLifecycleOwner = it ?: return@Observer

                    viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            binding = null
                        }
                    })
                }

            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observeForever(
                    viewLifecycleOwnerLiveDataObserver
                )
            }

            override fun onDestroy(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.removeObserver(
                    viewLifecycleOwnerLiveDataObserver
                )
            }
        })
    }

    private fun verifyLifecycleState() {
        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Cannot access view bindings. View lifecycle is ${lifecycle.currentState}!")
        }
    }
}



