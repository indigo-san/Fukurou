using System;
using System.Collections.Generic;

using Xamarin.Forms;

using XamarinApp1.ViewModels;
using XamarinApp1.Views;

namespace XamarinApp1
{
    public partial class AppShell : Shell
    {
        public AppShell()
        {
            InitializeComponent();

            Routing.RegisterRoute(nameof(SchoolDaysPage), typeof(SchoolDaysPage));
            Routing.RegisterRoute(nameof(SchoolDayDetailPage), typeof(SchoolDayDetailPage));

            Routing.RegisterRoute(nameof(ReportsPage), typeof(ReportsPage));
            Routing.RegisterRoute(nameof(ReportDetailPage), typeof(ReportDetailPage));

            Routing.RegisterRoute(nameof(LessonDetailPage), typeof(LessonDetailPage));
            Routing.RegisterRoute(nameof(NewLessonPage), typeof(NewLessonPage));

            Routing.RegisterRoute(nameof(SubjectDetailPage), typeof(SubjectDetailPage));
        }

        private async void OnMenuItemClicked(object sender, EventArgs e)
        {
            await GoToAsync("//LoginPage");
        }
    }
}
