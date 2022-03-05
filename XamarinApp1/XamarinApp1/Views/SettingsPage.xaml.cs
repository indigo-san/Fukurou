using Android.App;

using System;
using System.IO;

using Xamarin.Forms;

using XamarinApp1.Services;

using XF.Material.Forms.UI.Dialogs;

namespace XamarinApp1.Views;

public partial class SettingsPage : ContentPage
{
    public SettingsPage()
    {
        InitializeComponent();
    }

    private void ExportAsJson_Clicked(object sender, EventArgs e)
    {
        static void TryCopy(string src, string dest)
        {
            if (File.Exists(src))
            {
                File.Copy(src, dest, true);
            }
        }

        var context = DependencyService.Get<Activity>();
        var folderPath = context.GetExternalFilesDir(Android.OS.Environment.DirectoryDownloads).Path;
        if (!Directory.Exists(folderPath))
        {
            Directory.CreateDirectory(folderPath);
        }

        TryCopy(Path.Combine(StorageHelper.GetPath(), "lessons.json"), Path.Combine(folderPath, "lessons.json"));
        TryCopy(Path.Combine(StorageHelper.GetPath(), "reports.json"), Path.Combine(folderPath, "reports.json"));
        TryCopy(Path.Combine(StorageHelper.GetPath(), "schoolDays.json"), Path.Combine(folderPath, "schoolDays.json"));
        TryCopy(Path.Combine(StorageHelper.GetPath(), "subjects.json"), Path.Combine(folderPath, "subjects.json"));

        MaterialDialog.Instance.SnackbarAsync($"'{folderPath}' に保存されました。");
    }
}
