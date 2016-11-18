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
    view = finder.findRequiredView(source, 2131493317, "method 'onClick'");
    unbinder.view2131493317 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493318, "method 'onClick'");
    unbinder.view2131493318 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493319, "method 'onClick'");
    unbinder.view2131493319 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493320, "method 'onClick'");
    unbinder.view2131493320 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493321, "method 'onClick'");
    unbinder.view2131493321 = view;
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
    view = finder.findRequiredView(source, 2131493316, "method 'onClick'");
    unbinder.view2131493316 = view;
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

    View view2131493317;

    View view2131493318;

    View view2131493319;

    View view2131493320;

    View view2131493321;

    View view2131493322;

    View view2131493323;

    View view2131493324;

    View view2131493316;

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
      view2131493317.setOnClickListener(null);
      view2131493318.setOnClickListener(null);
      view2131493319.setOnClickListener(null);
      view2131493320.setOnClickListener(null);
      view2131493321.setOnClickListener(null);
      view2131493322.setOnClickListener(null);
      view2131493323.setOnClickListener(null);
      view2131493324.setOnClickListener(null);
      view2131493316.setOnClickListener(null);
    }
  }
}
