﻿using UnityEditor;
using UnityEngine;

[InitializeOnLoad]
public class CompilerOptionsEditorScript
{
    static bool waitingForStop = false;

    static CompilerOptionsEditorScript()
    {
        EditorApplication.update += OnEditorUpdate;
    }

    static void OnEditorUpdate()
    {
        if (!waitingForStop
            && EditorApplication.isCompiling
            && EditorApplication.isPlaying)
        {
            EditorApplication.isPlaying = false;
        }

        if (!waitingForStop
            && EditorApplication.isCompiling
            && EditorApplication.isPlaying)
        {
            EditorApplication.LockReloadAssemblies();
            EditorApplication.playmodeStateChanged
                 += PlaymodeChanged;
            waitingForStop = true;
        }
    }

    static void PlaymodeChanged()
    {
        if (EditorApplication.isPlaying)
            return;

        EditorApplication.UnlockReloadAssemblies();
        EditorApplication.playmodeStateChanged
             -= PlaymodeChanged;
        waitingForStop = false;
    }
}