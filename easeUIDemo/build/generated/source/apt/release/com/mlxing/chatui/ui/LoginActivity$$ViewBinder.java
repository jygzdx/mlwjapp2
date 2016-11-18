// Generated code from Butter Knife. Do not modify!
package com.mlxing.chatui.ui;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class LoginActivity$$ViewBinder<T extends LoginActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131492947, "field 'titleBar'");
    target.titleBar = finder.castView(view, 2131492947, "field 'titleBar'");
    view = finder.findRequiredView(source, 2131493136, "field 'editUsername'");
    target.editUsername = finder.castView(view, 2131493136, "field 'editUsername'");
    view = finder.findRequiredView(source, 2131493139, "field 'editPswd1'");
    target.editPswd1 = finder.castView(view, 2131493139, "field 'editPswd1'");
    view = finder.findRequiredView(source, 2131492962, "method 'onClick'");
    unbinder.view2131492962 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493141, "method 'onClick'");
    unbinder.view2131493141 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493142, "method 'onClick'");
    unbinder.view2131493142 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493145, "method 'onClick'");
    unbinder.view2131493145 = view;
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

  protected static class InnerUnbinder<T extends LoginActivity> implements Unbinder {
    private T target;

    View view2131492962;

    View view2131493141;

    View view2131493142;

    View view2131493145;

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
      view2131492962.setOnClickListener(null);
      view2131493141.setOnClickListener(null);
      view2131493142.setOnClickListener(null);
      view2131493145.setOnClickListener(null);
    }
  }
}
