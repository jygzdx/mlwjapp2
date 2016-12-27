// Generated code from Butter Knife. Do not modify!
package com.mlxing.chatui.daoyou.utils;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class PopupUtils$$ViewBinder<T extends PopupUtils> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131493323, "method 'onClick'");
    unbinder.view2131493323 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493324, "method 'onClick'");
    unbinder.view2131493324 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493325, "method 'onClick'");
    unbinder.view2131493325 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493326, "method 'onClick'");
    unbinder.view2131493326 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493327, "method 'onClick'");
    unbinder.view2131493327 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493328, "method 'onClick'");
    unbinder.view2131493328 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493329, "method 'onClick'");
    unbinder.view2131493329 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493330, "method 'onClick'");
    unbinder.view2131493330 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493322, "method 'onClick'");
    unbinder.view2131493322 = view;
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

  protected static class InnerUnbinder<T extends PopupUtils> implements Unbinder {
    private T target;

    View view2131493323;

    View view2131493324;

    View view2131493325;

    View view2131493326;

    View view2131493327;

    View view2131493328;

    View view2131493329;

    View view2131493330;

    View view2131493322;

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
      view2131493323.setOnClickListener(null);
      view2131493324.setOnClickListener(null);
      view2131493325.setOnClickListener(null);
      view2131493326.setOnClickListener(null);
      view2131493327.setOnClickListener(null);
      view2131493328.setOnClickListener(null);
      view2131493329.setOnClickListener(null);
      view2131493330.setOnClickListener(null);
      view2131493322.setOnClickListener(null);
    }
  }
}
