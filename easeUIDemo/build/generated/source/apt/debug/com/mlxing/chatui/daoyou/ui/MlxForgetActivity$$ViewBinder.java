// Generated code from Butter Knife. Do not modify!
package com.mlxing.chatui.daoyou.ui;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class MlxForgetActivity$$ViewBinder<T extends MlxForgetActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131492956, "field 'titleBar'");
    target.titleBar = finder.castView(view, 2131492956, "field 'titleBar'");
    view = finder.findRequiredView(source, 2131493125, "field 'editUsername'");
    target.editUsername = finder.castView(view, 2131493125, "field 'editUsername'");
    view = finder.findRequiredView(source, 2131493128, "field 'editPswd1'");
    target.editPswd1 = finder.castView(view, 2131493128, "field 'editPswd1'");
    view = finder.findRequiredView(source, 2131493281, "field 'editPswd2'");
    target.editPswd2 = finder.castView(view, 2131493281, "field 'editPswd2'");
    view = finder.findRequiredView(source, 2131493278, "field 'editCode'");
    target.editCode = finder.castView(view, 2131493278, "field 'editCode'");
    view = finder.findRequiredView(source, 2131493283, "field 'imageSign' and method 'onClick'");
    target.imageSign = finder.castView(view, 2131493283, "field 'imageSign'");
    unbinder.view2131493283 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493279, "field 'btnGetCode' and method 'onClick'");
    target.btnGetCode = finder.castView(view, 2131493279, "field 'btnGetCode'");
    unbinder.view2131493279 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends MlxForgetActivity> implements Unbinder {
    private T target;

    View view2131493283;

    View view2131493279;

    protected InnerUnbinder(T target) {
      this.target = target;
    }

    @Override
    public final void unbind() {
      if (target == null) throw new IllegalStateException("Bindings already cleared.");
      unbind(target);
      target = null;
    }

    protected void unbind(T target) {
      target.titleBar = null;
      target.editUsername = null;
      target.editPswd1 = null;
      target.editPswd2 = null;
      target.editCode = null;
      view2131493283.setOnClickListener(null);
      target.imageSign = null;
      view2131493279.setOnClickListener(null);
      target.btnGetCode = null;
    }
  }
}
