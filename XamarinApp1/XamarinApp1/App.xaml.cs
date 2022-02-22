using System;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using XamarinApp1.Services;
using XamarinApp1.Views;

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

            DependencyService.Register<MockDataStore>();
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
