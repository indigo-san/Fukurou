using Android.App;

using System;

using Xamarin.Forms;

using XamarinApp1.ViewModels;

namespace XamarinApp1.Views;

public partial class ReportsPage : ContentPage
{
    private bool isFirstLoading = true;

    public ReportsPage()
    {
        InitializeComponent();
        SizeChanged += ReportsPage_SizeChanged;
    }

    private void ReportsPage_SizeChanged(object sender, EventArgs e)
    {
        if (isFirstLoading)
        {
            ScrollToToday_Clicked(null, EventArgs.Empty);
            isFirstLoading = false;
            SizeChanged -= ReportsPage_SizeChanged;
        }
    }

    private void ScrollToToday_Clicked(object sender, EventArgs e)
    {
        ScrollToDate(DateOnly.FromDateTime(DateTime.Now));
    }

    private async void ScrollToDate(DateOnly date)
    {
        if (BindingContext is not ReportsViewModel vm) return;

        await vm.RefreshTask;
        if (vm.Items.Count >= 1)
        {
            if (vm.Items[0].Date >= date)
            {
                CollectionView1.ScrollTo(0, 0, ScrollToPosition.Start);
            }
            else if (vm.Items[^1].Date <= date)
            {
                CollectionView1.ScrollTo(0, vm.Items.Count - 1, ScrollToPosition.Start);
            }
            else
            {
                for (int i = 0; i < vm.Items.Count - 1; i++)
                {
                    var current = vm.Items[i];
                    var next = vm.Items[i + 1];

                    if (current.Date <= date && date <= next.Date)
                    {
                        CollectionView1.ScrollTo(0, i + 1, ScrollToPosition.Start);
                        break;
                    }
                }
            }
        }
    }

    private void ScrollToSpecDate_Clicked(object sender, EventArgs e)
    {
        var now = DateTime.Now;
        var dialog = new DatePickerDialog(DependencyService.Get<Activity>(), (s, e) =>
        {
            ScrollToDate(DateOnly.FromDateTime(e.Date));
        }, now.Year, now.Month - 1, now.Day);
        dialog.Show();
    }
}
