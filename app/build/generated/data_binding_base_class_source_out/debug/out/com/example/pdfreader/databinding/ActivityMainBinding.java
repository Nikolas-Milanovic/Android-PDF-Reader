// Generated by view binder compiler. Do not edit!
package com.example.pdfreader.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.pdfreader.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button backButton;

  @NonNull
  public final TextView currPage;

  @NonNull
  public final RadioButton draw;

  @NonNull
  public final RadioButton erase;

  @NonNull
  public final TextView fileName;

  @NonNull
  public final RadioButton highlight;

  @NonNull
  public final LinearLayout linearLayout;

  @NonNull
  public final LinearLayout linearLayout3;

  @NonNull
  public final RadioGroup modeSelection;

  @NonNull
  public final Button nextButton;

  @NonNull
  public final RadioButton pan;

  @NonNull
  public final LinearLayout pdfLayout;

  @NonNull
  public final Button redoButton;

  @NonNull
  public final Button undoButton;

  private ActivityMainBinding(@NonNull ConstraintLayout rootView, @NonNull Button backButton,
      @NonNull TextView currPage, @NonNull RadioButton draw, @NonNull RadioButton erase,
      @NonNull TextView fileName, @NonNull RadioButton highlight,
      @NonNull LinearLayout linearLayout, @NonNull LinearLayout linearLayout3,
      @NonNull RadioGroup modeSelection, @NonNull Button nextButton, @NonNull RadioButton pan,
      @NonNull LinearLayout pdfLayout, @NonNull Button redoButton, @NonNull Button undoButton) {
    this.rootView = rootView;
    this.backButton = backButton;
    this.currPage = currPage;
    this.draw = draw;
    this.erase = erase;
    this.fileName = fileName;
    this.highlight = highlight;
    this.linearLayout = linearLayout;
    this.linearLayout3 = linearLayout3;
    this.modeSelection = modeSelection;
    this.nextButton = nextButton;
    this.pan = pan;
    this.pdfLayout = pdfLayout;
    this.redoButton = redoButton;
    this.undoButton = undoButton;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.backButton;
      Button backButton = ViewBindings.findChildViewById(rootView, id);
      if (backButton == null) {
        break missingId;
      }

      id = R.id.currPage;
      TextView currPage = ViewBindings.findChildViewById(rootView, id);
      if (currPage == null) {
        break missingId;
      }

      id = R.id.draw;
      RadioButton draw = ViewBindings.findChildViewById(rootView, id);
      if (draw == null) {
        break missingId;
      }

      id = R.id.erase;
      RadioButton erase = ViewBindings.findChildViewById(rootView, id);
      if (erase == null) {
        break missingId;
      }

      id = R.id.fileName;
      TextView fileName = ViewBindings.findChildViewById(rootView, id);
      if (fileName == null) {
        break missingId;
      }

      id = R.id.highlight;
      RadioButton highlight = ViewBindings.findChildViewById(rootView, id);
      if (highlight == null) {
        break missingId;
      }

      id = R.id.linearLayout;
      LinearLayout linearLayout = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout == null) {
        break missingId;
      }

      id = R.id.linearLayout3;
      LinearLayout linearLayout3 = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout3 == null) {
        break missingId;
      }

      id = R.id.modeSelection;
      RadioGroup modeSelection = ViewBindings.findChildViewById(rootView, id);
      if (modeSelection == null) {
        break missingId;
      }

      id = R.id.nextButton;
      Button nextButton = ViewBindings.findChildViewById(rootView, id);
      if (nextButton == null) {
        break missingId;
      }

      id = R.id.pan;
      RadioButton pan = ViewBindings.findChildViewById(rootView, id);
      if (pan == null) {
        break missingId;
      }

      id = R.id.pdfLayout;
      LinearLayout pdfLayout = ViewBindings.findChildViewById(rootView, id);
      if (pdfLayout == null) {
        break missingId;
      }

      id = R.id.redoButton;
      Button redoButton = ViewBindings.findChildViewById(rootView, id);
      if (redoButton == null) {
        break missingId;
      }

      id = R.id.undoButton;
      Button undoButton = ViewBindings.findChildViewById(rootView, id);
      if (undoButton == null) {
        break missingId;
      }

      return new ActivityMainBinding((ConstraintLayout) rootView, backButton, currPage, draw, erase,
          fileName, highlight, linearLayout, linearLayout3, modeSelection, nextButton, pan,
          pdfLayout, redoButton, undoButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}