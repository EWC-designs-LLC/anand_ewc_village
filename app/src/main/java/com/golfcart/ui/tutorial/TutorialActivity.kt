package com.golfcart.ui.tutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.golfcart.BR
import com.golfcart.R
import com.golfcart.databinding.ActivityTutorialBinding
import com.golfcart.databinding.ItemTutorialViewPagerBinding
import com.golfcart.ui.base.BaseActivity
import com.golfcart.ui.base.adapter.RecyclerCallback
import com.golfcart.ui.base.adapter.RecyclerViewGenericAdapter
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.show
import com.golfcart.utils.village_constant.VillageConstants.IS_FROM_HOME
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class TutorialActivity : BaseActivity<ActivityTutorialBinding, TutorialViewModel>() {

    private var isFromHome: Boolean? = true
    var tutorialAdapter: RecyclerViewGenericAdapter<Int, ItemTutorialViewPagerBinding>? = null
    val imageList: ArrayList<Int> = ArrayList()

    var binding: ActivityTutorialBinding? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_tutorial
    }

    override fun getCurrentRunningActivity(): AppCompatActivity {
        return this@TutorialActivity
    }

    override fun getViewModel(): TutorialViewModel {
        val vm: TutorialViewModel by viewModel()
        return vm
    }

    override fun getBindingVariable(): Int {
        return BR.tutorial
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performBinding()
        setViews()
        setData()
        setListiner()
        setAdapter()
    }

    private fun setAdapter() {
        binding!!.apply {
            if (tutorialAdapter == null) {

                tutorialAdapter =
                    RecyclerViewGenericAdapter(imageList, R.layout.item_tutorial_view_pager,
                        object : RecyclerCallback<ItemTutorialViewPagerBinding, Int> {
                            override fun bindData(
                                binder: ItemTutorialViewPagerBinding,
                                model: Int,
                                position: Int,
                                itemView: View?
                            ) {
                                binder.apply {
                                    ivTutorial.setImageResource(model)

                                    if (position == imageList.size - 1) {
                                        tvNext.show()
                                    } else {
                                        tvNext.hide()
                                    }

                                    if (isFromHome!!){
                                        tvNext.hide()
                                    }

                                    tvNext.setOnClickListener {
                                        navigateToDashboard()
                                    }
                                }
                            }
                        })
            }

            viewPager.adapter = tutorialAdapter
            TabLayoutMediator(tabIndicators, viewPager) { tab, position ->

            }.attach()
        }
    }

    private fun setListiner() {
        binding!!.apply {
            layoutBack.setOnClickListener {
                finish()
            }

            tvSkip.setOnClickListener {
                navigateToDashboard()
            }
        }
    }

    private fun setViews() {
        binding!!.apply {
            val intent = intent
            isFromHome = intent.getBooleanExtra(IS_FROM_HOME, false)
            if (isFromHome!!) {
                layoutBack.show()
                tvSkip.hide()
            }
        }
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this@TutorialActivity, VillageActivity::class.java))
        finishAffinity()
    }

    private fun setData() {
        imageList.clear()
        imageList.add(R.drawable.tutorial_1)
        imageList.add(R.drawable.tutorial_2)
        imageList.add(R.drawable.tutorial_3)
        imageList.add(R.drawable.tutorial_4)
        imageList.add(R.drawable.tutorial_5)
        imageList.add(R.drawable.tutorial_6)
    }

    private fun performBinding() {
        binding = DataBindingUtil.setContentView(this@TutorialActivity, R.layout.activity_tutorial)
        binding?.executePendingBindings()
    }
}