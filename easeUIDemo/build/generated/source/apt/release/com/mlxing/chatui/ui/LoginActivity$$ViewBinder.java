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
    view = finder.findRequiredView(source, 2131492985, "field 'titleBar'");
    target.titleBar = finder.castView(view, 2131492985, "field 'titleBar'");
    view = finder.findRequiredView(source, 2131493120, "field 'editUsername'");
    target.editUsername = finder.castView(view, 2131493120, "field 'editUsername'");
    view = finder.findRequiredView(source, 2131493123, "field 'editPswd1'");
    target.editPswd1 = finder.castView(view, 2131493123, "field 'editPswd1'");
    view = finder.findRequiredView(source, 2131492958, "method 'onClick'");
    unbinder.view2131492958 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493125, "method 'onClick'");
    unbinder.view2131493125 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493126, "method 'onClick'");
    unbinder.view2131493126 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493129, "method 'onClick'");
    unbinder.view2131493129 = view;
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

    View view2131492958;

    View view2131493125;

    View view2131493126;

    View view2131493129;

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
      view2131492958.setOnClickListener(null);
      view2131493125.setOnClickListener(null);
      view2131493126.setOnClickListener(null);
      view2131493129.setOnClickListener(null);
    }
  }
}
