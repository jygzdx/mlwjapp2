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
    view = finder.findRequiredView(source, 2131493293, "method 'onClick'");
    unbinder.view2131493293 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493294, "method 'onClick'");
    unbinder.view2131493294 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493295, "method 'onClick'");
    unbinder.view2131493295 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493296, "method 'onClick'");
    unbinder.view2131493296 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493297, "method 'onClick'");
    unbinder.view2131493297 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493298, "method 'onClick'");
    unbinder.view2131493298 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493299, "method 'onClick'");
    unbinder.view2131493299 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493300, "method 'onClick'");
    unbinder.view2131493300 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131493292, "method 'onClick'");
    unbinder.view2131493292 = view;
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

    View view2131493293;

    View view2131493294;

    View view2131493295;

    View view2131493296;

    View view2131493297;

    View view2131493298;

    View view2131493299;

    View view2131493300;

    View view2131493292;

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
      view2131493293.setOnClickListener(null);
      view2131493294.setOnClickListener(null);
      view2131493295.setOnClickListener(null);
      view2131493296.setOnClickListener(null);
      view2131493297.setOnClickListener(null);
      view2131493298.setOnClickListener(null);
      view2131493299.setOnClickListener(null);
      view2131493300.setOnClickListener(null);
      view2131493292.setOnClickListener(null);
    }
  }
}
