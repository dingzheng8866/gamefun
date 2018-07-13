using GEngine.Util;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using UnityEditor;
using UnityEngine;

namespace GEngine.Asset
{
    public class BuildAssetBundleName
    {

        public static string GetAssetBundleName(AssetNode assetNode, string resourceRoot, string ext)
        {
            string path = assetNode.path;
            string assetBundleName = path.Replace(resourceRoot + "/", "").ToLower();

            if (assetBundleName.StartsWith("avatar") && !path.EndsWith(".prefab"))
            {
                string[] args = assetBundleName.Split('/');
                if (args.Length > 3)
                {
                    assetBundleName = args[0] + "/" + args[1] + "/" + args[2];
                }
            }
            else if (assetBundleName.StartsWith("config"))
            {
                assetBundleName = "config/config";
            }
            else if (assetNode.parentCount == 0 && !path.EndsWith(".prefab"))
            {
                string[] args = assetBundleName.Split('/');
                string s = "";
                int count = args.Length - 1;
                for(int i=0; i< count; i++)
                {
                    s += args[i];
                    //if(i!= count -1)
                    //{
                        s += "/";
                    //}
                }
                s += args[count-1];
                assetBundleName = s;
            }

            assetBundleName = PathUtil.ChangeExtension(assetBundleName, ext);
            return assetBundleName;
        }

        static string[] filterDirList = new string[] { };
        static List<string> filterExts = new List<string> { ".cs", ".js" };
        static List<string> imageExts = new List<string> { ".png", ".jpg", ".jpeg", ".bmp", "gif", ".tga", ".tiff", ".psd" };
        static bool isSpriteTag = true;

        public static List<string> exts = new List<string>(new string[] { ".prefab", ".png", ".jpg", ".jpeg", ".bmp", "gif", ".tga", ".tiff", ".psd", ".mat", ".mp3", ".wav", ".shader", ".ttf", ".csv", ".xml", ".txt" });

        public static void MakeAssetBundleNamesByAutoDependenceTree()
        {
            List<string> list = new List<string>();
            PathUtil.RecursiveFile(BuildTool.ResourcesBuildDir, list, exts);

            if (list.Count == 0)
                return;


            Dictionary<string, string> originalAssetPathBundleNameMap = GetCurrentAssetPathBundleNameMap();
            Debug.Log("originalAssetPathBundleNameMap 1:" + originalAssetPathBundleNameMap.Count);

            // 生成所有节点
            Dictionary<string, AssetNode> nodeDict = AssetNodeUtil.GenerateAllNode(originalAssetPathBundleNameMap, list, filterDirList, filterExts, imageExts, isSpriteTag, GEngineDef.AssetBundleExt);

            // 生成每个节点依赖的节点
            AssetNodeUtil.GenerateNodeDependencies(nodeDict);


            // 寻找入度为0的节点
            List<AssetNode> roots = AssetNodeUtil.FindRoots(nodeDict);

            // 移除父节点的依赖和自己依赖相同的节点
            AssetNodeUtil.RemoveParentShare(roots);


            // 强制设置某些节点为Root节点，删掉被依赖
            AssetNodeUtil.ForcedSetRoots(nodeDict, list, imageExts);


            // 寻找入度为0的节点
            roots = AssetNodeUtil.FindRoots(nodeDict);

            // 入度为1的节点自动打包到上一级节点
            AssetNodeUtil.MergeParentCountOnce(roots);

            // 生成需要设置AssetBundleName的节点
            Dictionary<string, AssetNode> assetDict = AssetNodeUtil.GenerateAssetBundleNodes(roots);

            Debug.Log("originalAssetPathBundleNameMap 2:" + originalAssetPathBundleNameMap.Count);
            Debug.Log("assetDict:" + assetDict.Count);

            foreach (var v in assetDict)
            {
                if(v.Value.parentCount < 1)
                {
                    //Debug.Log(v.Value.path + " ========================== no parent");
                }
            }

            // 设置AssetBundleNames
            AssetNodeUtil.SetAssetBundleNames(originalAssetPathBundleNameMap, assetDict, BuildTool.ResourcesRootDir, GEngineDef.AssetBundleExt);

            foreach(var v in originalAssetPathBundleNameMap)
            {
                if(!assetDict.ContainsKey(v.Key))
                {
                    Debug.Log("======================> " + v.Key + " ==> " + v.Value);
                }
            }

            foreach (var v in assetDict)
            {
                AssetImporter importer = AssetImporter.GetAtPath(v.Value.path);
                Debug.Log(v.Value.path.Replace(BuildTool.ResourcesBuildDir, "") + " ==> " + importer.assetBundleName);
            }

            AssetDatabase.RemoveUnusedAssetBundleNames();
            AssetDatabase.Refresh();
        }

        public static Dictionary<string, string> GetCurrentAssetPathBundleNameMap()
        {
            Dictionary<string, string> dict = new Dictionary<string, string>();
            string[] names = AssetDatabase.GetAllAssetBundleNames();

            int count = names.Length;
            for (int i = 0; i < count; i++)
            {
                if (names[i].IndexOf(GEngineDef.AssetBundleExt) != -1)
                {
                    string[] assets = AssetDatabase.GetAssetPathsFromAssetBundle(names[i]);
                    for (int j = 0; j < assets.Length; j++)
                    {
                        dict[assets[j]] = AssetImporter.GetAtPath(assets[j]).assetBundleName;
                        //dict[assets[j]] = names[i];
                        if(assets.Length > 1)
                        {
                            //Debug.Log("Asset : " + assets[j] + " ==> ab: " + AssetImporter.GetAtPath(assets[j]).assetBundleName + " ==> asset count: " + assets.Length);
                        }
                    }
                }
                else
                {
                    Debug.Log("Check Asset : " + names[i] + " ==> ab: " + AssetImporter.GetAtPath(names[i]).assetBundleName + " ==> asset count: " + AssetDatabase.GetAssetPathsFromAssetBundle(names[i]));
                }
            }
            return dict;
        }

    }
}
