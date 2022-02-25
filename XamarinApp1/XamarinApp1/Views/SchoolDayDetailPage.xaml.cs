using Android.App;

using System;
using System.Linq;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.ViewModels;

namespace XamarinApp1.Views;

public partial class SchoolDayDetailPage : ContentPage
{
    private IDisposable disposable;

    public SchoolDayDetailPage()
    {
        InitializeComponent();
    }

    protected override void OnBindingContextChanged()
    {
        base.OnBindingContextChanged();
        disposable?.Dispose();
        if (BindingContext is SchoolDayDetailViewModel vm)
        {
            disposable = vm.SchoolDay.Subscribe(_ =>
            {
                LessonListView.NativeSizeChanged();
                ReportListView.NativeSizeChanged();
            });
        }
    }

    private void ChangeDate_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is SchoolDayDetailViewModel vm && vm.SchoolDay.Value is SchoolDay sc)
        {
            var dialog = new DatePickerDialog(DependencyService.Get<Activity>(), async (s, e) =>
            {
                bool updateReports = false;

                if (sc.Reports.Any())
                {
                    updateReports = await DisplayAlert("日付を変更", "レポートの日付も変更しますか？", "Yes", "No");
                }

                vm.UpdateDate(DateOnly.FromDateTime(e.Date), updateReports);
            }, sc.Date.Year, sc.Date.Month - 1, sc.Date.Day);
            dialog.Show();
        }
    }

    private void ReportListView_ItemSelected(object sender, SelectedItemChangedEventArgs e)
    {
        if (BindingContext is SchoolDayDetailViewModel vm && e.SelectedItem is Report report)
        {
            ReportListView.SelectedItem = null;
            vm.GoToReportDetail.Execute(report);
        }
    }

    private void LessonListView_ItemSelected(object sender, SelectedItemChangedEventArgs e)
    {
        if (BindingContext is SchoolDayDetailViewModel vm && e.SelectedItem is Lesson lesson)
        {
            LessonListView.SelectedItem = null;
            vm.GoToLessonDetail.Execute(lesson);
        }
    }

    private void NewLesson_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is SchoolDayDetailViewModel vm)
        {
            Shell.Current.GoToAsync($"{nameof(NewLessonPage)}?Date={Uri.EscapeDataString(vm.SchoolDay.Value.Date.ToString())}");
        }
    }
}
