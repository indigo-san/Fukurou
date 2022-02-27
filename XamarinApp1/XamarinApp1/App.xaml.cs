using System;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Services;

using XF.Material.Forms;
using XF.Material.Forms.Resources;

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
