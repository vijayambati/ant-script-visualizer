package com.nurflugel.util.gradlescriptvisualizer.ui;

import com.nurflugel.util.ScriptPreferences;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 6/6/12 Time: 18:41 To change this template use File | Settings | File Templates. */
public class GradleScriptPreferences extends ScriptPreferences
{
  private static final String WATCH_FILES_FOR_CHANGES = "watch files for changes";
  private boolean             watchFilesForChanges;

  public GradleScriptPreferences()
  {
    super(GradleScriptMainFrame.class);
    watchFilesForChanges = preferencesStore.getBoolean(WATCH_FILES_FOR_CHANGES, false);
  }

  @Override
  public void save()
  {
    super.save();
    preferencesStore.putBoolean(WATCH_FILES_FOR_CHANGES, watchFilesForChanges);
  }

  public void setWatchFilesForChanges(boolean watchFilesForChanges)
  {
    this.watchFilesForChanges = watchFilesForChanges;
    save();
  }

  public boolean watchFilesForChanges()
  {
    return watchFilesForChanges;
  }
}
