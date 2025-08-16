package ir.pooyadev.presentation.view.base

import android.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater


abstract class BaseFragment<VDB : ViewDataBinding>(
    @LayoutRes private val resId: Int,
) : Fragment() {

    lateinit var bindingFragment: VDB
    lateinit var fragmentContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        bindingFragment = DataBindingUtil.inflate(
            inflater, resId, container, false
        )
        fragmentContext = requireContext()
        sharedElementEnterTransition =
            TransitionInflater.from(fragmentContext).inflateTransition(R.transition.explode)
//        postponeEnterTransition(250, TimeUnit.MILLISECONDS)
        return bindingFragment.root
    }

}