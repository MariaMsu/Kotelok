package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.selection.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class SelectionItemAnimator : DefaultItemAnimator() {
    class AnimationEndListener(private val onAnimationEnd: () -> Unit) : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            onAnimationEnd()
        }
    }
    class SelectionItemHolderInfo(val animation: Int) : ItemHolderInfo()

//    override fun animateChange(
//        oldHolder: RecyclerView.ViewHolder,
//        newHolder: RecyclerView.ViewHolder,
//        preInfo: ItemHolderInfo,
//        postInfo: ItemHolderInfo
//    ): Boolean {
//        val viewHolder = newHolder as? WordDefinitionViewHolder
//
//        if (viewHolder != null) {
//            if (preInfo is SelectionItemHolderInfo) {
//                when (preInfo.animation) {
//                    Animations.ANIMATION_BUTTON_TO_CHECKBOX -> {
//                        if (!viewHolder.isSelectionShowed()) {
// //                            val onAnimationEndListener = AnimationEndListener {
// //                                dispatchAnimationFinished(viewHolder)
// //                            }
//                            Log.d("SELECTION_ANIMATION", "start animation")
//                            viewHolder.replaceOnCheckbox{
//                                dispatchAnimationFinished(viewHolder)
//                            }.start()
//                        }
//                    }
//                    Animations.ANIMATION_CHECKBOX_TO_BUTTON -> {
//                        if (viewHolder.isSelectionShowed()) {
// //                            val onAnimationEnd = AnimationEndListener {
// //                                dispatchAnimationFinished(viewHolder)
// //                            }
//                            Log.d("SELECTION_ANIMATION", "start animation")
//                            viewHolder.replaceOnButton{
//                                dispatchAnimationFinished(viewHolder)
//                            }.start()
//                        }
//                    }
//                }
//                return true
//            }
//        }
//
//        return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
//    }

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder, payloads: MutableList<Any>): Boolean {
        return true
    }

    @Suppress("ReturnCount")
    override fun recordPreLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder,
        changeFlags: Int,
        payloads: MutableList<Any>
    ): ItemHolderInfo {
        if (changeFlags == FLAG_CHANGED) {
            for (payload in payloads) {
                when (payload as? Int) {
                    Animations.ANIMATION_BUTTON_TO_CHECKBOX -> {
                        return SelectionItemHolderInfo(Animations.ANIMATION_BUTTON_TO_CHECKBOX)
                    }
                    Animations.ANIMATION_CHECKBOX_TO_BUTTON -> {
                        return SelectionItemHolderInfo(Animations.ANIMATION_CHECKBOX_TO_BUTTON)
                    }
                }
            }
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }
}
