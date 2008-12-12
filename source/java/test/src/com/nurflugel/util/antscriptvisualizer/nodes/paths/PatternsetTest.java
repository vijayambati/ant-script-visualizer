package com.nurflugel.util.antscriptvisualizer.nodes.paths;

import static com.nurflugel.util.antscriptvisualizer.Constants.*;
import org.jdom.Element;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

import java.io.File;


/** Patternset Tester. */
public class PatternsetTest
{


    // <patternset id="non.test.sources">
    ///// <include name="**/*.java"/>
    ///// <exclude name="**/*Test*"/>
    // </patternset>
    @Test
    public void testSimpleIncludesExcludes()
    {

        Element element = new Element(PATTERNSET);
        element.setAttribute(ID, "non.test.sources");

        Element includeElement = new Element(INCLUDE);
        includeElement.setAttribute(NAME, "**/*.java");
        element.addContent(includeElement);

        Element excludeElement = new Element(EXCLUDE);
        excludeElement.setAttribute(NAME, "**/*Test*");
        element.addContent(excludeElement);


        Patternset patternset = new Patternset(element);

        assertTrue(patternset.isFileOk(new File("Fibble.java")));
        assertFalse(patternset.isFileOk(new File("Fibble.xml")));
        assertFalse(patternset.isFileOk(new File("FibbleTest.java")));

    }


    // <patternset id="non.test.sources" includes ="**/*Dibble*.java,**/*Dabble*.*" excludes="**/*Test*"/>
    @Test
    public void testInlinedIncludesExcludes()
    {

        Element element = new Element(PATTERNSET);
        element.setAttribute(ID, "non.test.sources");
        element.setAttribute(INCLUDES, "**/*Dibble*.java,**/*Dabble*.java");
        element.setAttribute(EXCLUDES, "**/*Test*");

        Patternset patternset = new Patternset(element);

        assertTrue(patternset.isFileOk(new File("Dibble.java")));
        assertTrue(patternset.isFileOk(new File("DibbleBibble.java")));
        assertFalse(patternset.isFileOk(new File("Dabble.xml")));
        assertFalse(patternset.isFileOk(new File("DibbleTest.java")));

    }

}
