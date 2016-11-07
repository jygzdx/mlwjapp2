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
    view = finder.findRequiredView(source, 2131493305, "method 'onClick'");
    unbinder.view2131493305 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493306, "method 'onClick'");
    unbinder.view2131493306 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493307, "method 'onClick'");
    unbinder.view2131493307 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493308, "method 'onClick'");
    unbinder.view2131493308 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493309, "method 'onClick'");
    unbinder.view2131493309 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493310, "method 'onClick'");
    unbinder.view2131493310 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493311, "method 'onClick'");
    unbinder.view2131493311 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493312, "method 'onClick'");
    unbinder.view2131493312 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493304, "method 'onClick'");
    unbinder.view2131493304 = view;
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

    View view2131493305;

    View view2131493306;

    View view2131493307;

    View view2131493308;

    View view2131493309;

    View view2131493310;

    View view2131493311;

    View view2131493312;

    View view2131493304;

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
      view2131493305.setOnClickListener(null);
      view2131493306.setOnClickListener(null);
      view2131493307.setOnClickListener(null);
      view2131493308.setOnClickListener(null);
      view2131493309.setOnClickListener(null);
      view2131493310.setOnClickListener(null);
      view2131493311.setOnClickListener(null);
      view2131493312.setOnClickListener(null);
      view2131493304.setOnClickListener(null);
    }
  }
}
