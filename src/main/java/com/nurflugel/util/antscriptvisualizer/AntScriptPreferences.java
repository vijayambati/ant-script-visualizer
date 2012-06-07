package com.nurflugel.util.antscriptvisualizer;

import com.nurflugel.util.OutputFormat;
import com.nurflugel.util.ScriptPreferences;
import static com.nurflugel.util.OutputFormat.PDF;
import static com.nurflugel.util.OutputFormat.PNG;
import static java.util.prefs.Preferences.userNodeForPackage;

/**
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 24, 2009 Time: 5:04:47 PM To change this template use File | Settings | File Templates.
 */
public class AntScriptPreferences extends ScriptPreferences
{
  private static final String SHOW_ANTCALLS       = "showAntcalls";
  private static final String SHOW_ANTS           = "showAnts";
  private static final String SHOW_MACRODEFS      = "showMacrodefs";
  private static final String SHOW_TARGETS        = "showTargets";
  private static final String SHOW_TASKDEFS       = "showTaskdefs";
  private boolean             shouldShowMacrodefs = true;
  private boolean             shouldShowAntCalls  = true;
  private boolean             shouldShowTargets   = true;
  private boolean             shouldShowTaskdefs  = true;
  private boolean             shouldShowAnts      = true;

  public AntScriptPreferences()
  {
    super(AntParserUiImpl.class);
    shouldShowMacrodefs = preferencesStore.getBoolean(SHOW_MACRODEFS, true);
    shouldShowTaskdefs  = preferencesStore.getBoolean(SHOW_TASKDEFS, true);
    shouldShowAntCalls  = preferencesStore.getBoolean(SHOW_ANTCALLS, true);
    shouldShowAnts      = preferencesStore.getBoolean(SHOW_ANTS, true);
    shouldShowTargets   = preferencesStore.getBoolean(SHOW_TARGETS, true);
  }

  public AntScriptPreferences(boolean dummyFlag)
  {
    super(AntParserUiImpl.class);
  }

  // -------------------------- OTHER METHODS --------------------------
  public void setShouldShowAntCalls(boolean shouldShowAntCalls)
  {
    this.shouldShowAntCalls = shouldShowAntCalls;
    save();
  }

  @Override
  public void save()
  {
    super.save();
    preferencesStore.putBoolean(SHOW_MACRODEFS, shouldShowMacrodefs);
    preferencesStore.putBoolean(SHOW_TASKDEFS, shouldShowTaskdefs);
    preferencesStore.putBoolean(SHOW_ANTCALLS, shouldShowAntCalls);
    preferencesStore.putBoolean(SHOW_ANTS, shouldShowAnts);
    preferencesStore.putBoolean(SHOW_TARGETS, shouldShowTargets);
  }

  public void setShouldShowAnts(boolean shouldShowAnts)
  {
    this.shouldShowAnts = shouldShowAnts;
    save();
  }

  public void setShouldShowMacrodefs(boolean shouldShowMacrodefs)
  {
    this.shouldShowMacrodefs = shouldShowMacrodefs;
    save();
  }

  public void setShouldShowTargets(boolean shouldShowTargets)
  {
    this.shouldShowTargets = shouldShowTargets;
    save();
  }

  public void setShouldShowTaskdefs(boolean shouldShowTaskdefs)
  {
    this.shouldShowTaskdefs = shouldShowTaskdefs;
    save();
  }

  public boolean shouldShowAntcalls()
  {
    return shouldShowAntCalls;
  }

  public boolean shouldShowAnts()
  {
    return shouldShowAnts;
  }

  public boolean shouldShowMacrodefs()
  {
    return shouldShowMacrodefs;
  }

  public boolean shouldShowTargets()
  {
    return shouldShowTargets;
  }

  public boolean shouldShowTaskdefs()
  {
    return shouldShowTaskdefs;
  }
}
