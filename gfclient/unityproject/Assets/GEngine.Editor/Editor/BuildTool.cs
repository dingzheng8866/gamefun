using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using UnityEditor;
using UnityEngine;

namespace GEngine.Asset
{
    public class BuildTool
    {

        static string ResourcesBuildDir
        {
            get
            {
                var dir = "Assets/" + GEngineDef.ResourcesBuildDir + "/";
                return dir;
            }
        }

        public static void MakeAssetBundleNames()
        {
            var dir = ResourcesBuildDir;

            // Check marked asset bundle whether real
            foreach (var assetGuid in AssetDatabase.FindAssets(""))
            {
                var assetPath = AssetDatabase.GUIDToAssetPath(assetGuid);
                var assetImporter = AssetImporter.GetAtPath(assetPath);
                var bundleName = assetImporter.assetBundleName;
                if (string.IsNullOrEmpty(bundleName))
                {
                    continue;
                }
                if (!assetPath.StartsWith(dir))
                {
                    assetImporter.assetBundleName = null;
                }
            }

            // set BundleResources's all bundle name
            foreach (var filepath in Directory.GetFiles(dir, "*.*", SearchOption.AllDirectories))
            {
                if (filepath.EndsWith(".meta")) continue;

                var importer = AssetImporter.GetAtPath(filepath);
                if (importer == null)
                {
                    Debug.LogError("Not found: "+filepath);
                    continue;
                }
                var bundleName = filepath.Substring(dir.Length, filepath.Length - dir.Length);
                Debug.Log(bundleName);
                if(bundleName.IndexOf("kuijiabing_1_1") > 0)
                {
                    importer.assetBundleName = "avatar/soldier/kuijiabing_1_1" + GEngineDef.AssetBundleExt;
                }
                else if(bundleName.IndexOf("kuijiabing_1_2") > 0)
                {
                    importer.assetBundleName = "avatar/soldier/kuijiabing_1_2" + GEngineDef.AssetBundleExt;
                }
                else
                {
                    importer.assetBundleName = bundleName + GEngineDef.AssetBundleExt;
                }
                

                //bool needBuild = AssetVersionControl.TryCheckNeedBuildWithMeta(filepath);
                //if (needBuild)
                //{
                //    AssetVersionControl.TryMarkBuildVersion(filepath);
                //    Debug.Log("version: " + filepath);
                //}
                //Debug.Log(importer.assetBundleName);
            }

            Debug.Log("Make all asset name successs!");

        }

        [MenuItem("GEngine/AssetBundle/Build All %&z")]
        public static void BuildAllAssetBundles()
        {
            if (EditorApplication.isPlaying)
            {
                Debug.LogError("Cannot build in playing mode! Please stop!");
                return;
            }
            //AssetVersionControl ac = new AssetVersionControl(true);
            MakeAssetBundleNames();
            //ac.Dispose();
            var outputPath = GetExportPath(EditorUserBuildSettings.activeBuildTarget);
            Debug.Log("Asset bundle start build to: "+ outputPath);
            BuildPipeline.BuildAssetBundles(outputPath, BuildAssetBundleOptions.DeterministicAssetBundle, EditorUserBuildSettings.activeBuildTarget);
            AssetDatabase.Refresh();

            SymbolLinkResource();
        }


        #region symbollink-ab
        public static string GetExportPath(BuildTarget platfrom) //KResourceQuality quality = KResourceQuality.Sd
        {
            string basePath = Path.GetFullPath(GEngineDef.AssetBundleBuildRelPath);

            if (File.Exists(basePath))
            {
                BuildTool.ShowDialog("路径配置错误: " + basePath);
                throw new System.Exception("路径配置错误");
            }
            if (!Directory.Exists(basePath))
            {
                Directory.CreateDirectory(basePath);
            }

            string path = null;
            var platformName = AssetManager.GetBuildPlatformName();
            //if (quality != KResourceQuality.Sd) // SD no need add
            //    platformName += quality.ToString().ToUpper();

            path = basePath + "/" + platformName + "/";
            if (!Directory.Exists(path))
            {
                Directory.CreateDirectory(path);
            }
            return path;
        }

        public static bool ShowDialog(string msg, string title = "提示", string button = "确定")
        {
            return EditorUtility.DisplayDialog(title, msg, button);
        }

        public static void ShowDialogSelection(string msg, Action yesCallback)
        {
            if (EditorUtility.DisplayDialog("确定吗", msg, "是!", "不！"))
            {
                yesCallback();
            }
        }


        public static void SymbolLinkResource()
        {
            DeleteAllLinks(AssetBundlesLinkPath);

            var exportPath = GetResourceExportPath();
            var linkPath = GetLinkPath();

            SymbolLinkFolder(exportPath, linkPath);

            AssetDatabase.Refresh();
        }

        public static string GetLinkPath()
        {
            if (!Directory.Exists(AssetBundlesLinkPath))
                Directory.CreateDirectory(AssetBundlesLinkPath);
            return AssetBundlesLinkPath + "/" + AssetManager.GetBuildPlatformName() + "/";
        }

        public static string GetResourceExportPath()
        {
            var resourcePath = GetExportPath(EditorUserBuildSettings.activeBuildTarget);
            return resourcePath;
        }

        public static string AssetBundlesLinkPath = "Assets/StreamingAssets/" +GEngineDef.StreamingBundlesFolderName;


        public static void DeleteAllLinks(string assetBundlesLinkPath)
        {
            if (Directory.Exists(assetBundlesLinkPath))
            {
                foreach (var dirPath in Directory.GetDirectories(assetBundlesLinkPath))
                {
                    DeleteLink(dirPath);
                }
            }

        }

        public static void DeleteLink(string linkPath)
        {
            var os = Environment.OSVersion;
            if (os.ToString().Contains("Windows"))
            {
                GUnityEditorTools.GEditorUtils.ExecuteCommand(String.Format("rmdir \"{0}\"", linkPath));
            }
            else if (os.ToString().Contains("Unix"))
            {
                GUnityEditorTools.GEditorUtils.ExecuteCommand(String.Format("rm -Rf \"{0}\"", linkPath));
            }
            else
            {
                Debug.LogError(String.Format("[SymbolLinkFolder]Error on OS: {0}", os.ToString()));
            }
        }

        public static void SymbolLinkFolder(string srcFolderPath, string targetPath)
        {
            var os = Environment.OSVersion;
            if (os.ToString().Contains("Windows"))
            {
                GUnityEditorTools.GEditorUtils.ExecuteCommand(String.Format("mklink /J \"{0}\" \"{1}\"", targetPath, srcFolderPath));
            }
            else if (os.ToString().Contains("Unix"))
            {
                var fullPath = Path.GetFullPath(targetPath);
                if (fullPath.EndsWith("/"))
                {
                    fullPath = fullPath.Substring(0, fullPath.Length - 1);
                    fullPath = Path.GetDirectoryName(fullPath);
                }
                GUnityEditorTools.GEditorUtils.ExecuteCommand(String.Format("ln -s {0} {1}", Path.GetFullPath(srcFolderPath), fullPath));
            }
            else
            {
                Debug.LogError(String.Format("[SymbolLinkFolder]Error on OS: {0}", os.ToString()));
            }
        }
        #endregion

    }
}
