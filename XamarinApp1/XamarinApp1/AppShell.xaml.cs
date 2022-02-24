﻿using System;
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

            Routing.RegisterRoute(nameof(ItemDetailPage), typeof(ItemDetailPage));
            Routing.RegisterRoute(nameof(NewReportPage), typeof(NewReportPage));
            Routing.RegisterRoute(nameof(SchoolDaysPage), typeof(SchoolDaysPage));
            Routing.RegisterRoute(nameof(SchoolDayDetailPage), typeof(SchoolDayDetailPage));
            Routing.RegisterRoute(nameof(ReportsPage), typeof(ReportsPage));
            Routing.RegisterRoute(nameof(ReportDetailPage), typeof(ReportDetailPage));
        }

        private async void OnMenuItemClicked(object sender, EventArgs e)
        {
            await GoToAsync("//LoginPage");
        }
    }
}
