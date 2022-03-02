
using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Services;

using XF.Material.Forms;
using XF.Material.Forms.Resources;
using XF.Material.Forms.UI.Dialogs;
using XF.Material.Forms.UI.Dialogs.Configurations;

namespace XamarinApp1
{
    public partial class App : Application
    {

        public App()
        {
            UserAppTheme = OSAppTheme.Dark;
            InitializeComponent();
            Material.Init(this, (MaterialConfiguration)Resources["Material.Configuration"]);

            //DependencyService.Register<IDataStore<SchoolDay>, MockSchoolDayDataStore>();
            //DependencyService.Register<IDataStore<Lesson>, MockLessonDataStore>();
            //DependencyService.Register<IDataStore<Subject>, MockSubjectDataStore>();
            //DependencyService.Register<IDataStore<Report>, MockReportDataStore>();

            DependencyService.Register<IDataStore<SchoolDay>, StorageSchoolDayDataStore>();
            DependencyService.Register<IDataStore<Lesson>, StorageLessonDataStore>();
            DependencyService.Register<IDataStore<Subject>, StorageSubjectDataStore>();
            DependencyService.Register<IDataStore<Report>, StorageReportDataStore>();
            MaterialDialog.Instance.SetGlobalStyles(
                dialogConfiguration: new MaterialAlertDialogConfiguration
                {
                    BackgroundColor = (Color)Resources["CardBackgroundColor"],
                    MessageTextColor = (Color)Resources["TextColor"],
                    TitleTextColor = (Color)Resources["TextColor"],
                },
                loadingDialogConfiguration: new MaterialLoadingDialogConfiguration()
                {
                    BackgroundColor = (Color)Resources["CardBackgroundColor"],
                },
                snackbarConfiguration: new MaterialSnackbarConfiguration()
                {
                    BackgroundColor = (Color)Resources["CardBackgroundColor"],
                    MessageTextColor = (Color)Resources["TextColor"],
                },
                simpleDialogConfiguration: new MaterialSimpleDialogConfiguration()
                {
                    BackgroundColor = (Color)Resources["CardBackgroundColor"],
                    TextColor = (Color)Resources["TextColor"],
                    TitleTextColor = (Color)Resources["TextColor"],
                },
                confirmationDialogConfiguration: new MaterialConfirmationDialogConfiguration()
                {
                    BackgroundColor = (Color)Resources["CardBackgroundColor"],
                    TextColor = (Color)Resources["TextColor"],
                    TitleTextColor = (Color)Resources["TextColor"],
                },
                inputDialogConfiguration: new MaterialInputDialogConfiguration()
                {
                    BackgroundColor = (Color)Resources["CardBackgroundColor"],
                    MessageTextColor = (Color)Resources["TextColor"],
                    InputTextColor = (Color)Resources["TextColor"],
                    TitleTextColor = (Color)Resources["TextColor"],
                },
                customContentDialogConfiguration: new MaterialAlertDialogConfiguration
                {
                    BackgroundColor = (Color)Resources["CardBackgroundColor"],
                    MessageTextColor = (Color)Resources["TextColor"],
                    TitleTextColor = (Color)Resources["TextColor"],
                });

            MainPage = new AppShell();
        }

        protected override void OnStart()
        {
        }

        protected override void OnSleep()
        {
        }

        protected override void OnResume()
        {
        }
    }
}
