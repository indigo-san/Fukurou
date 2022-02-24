
using Android.App;

using System;
using System.Linq;

using Xamarin.Forms;
using Xamarin.Forms.Platform.Android;
using Xamarin.Forms.Xaml;

using XamarinApp1.Models;
using XamarinApp1.ViewModels;

namespace XamarinApp1.Views;



//[XamlCompilation(XamlCompilationOptions.Skip)]
public partial class SchoolDayDetailPage : ContentPage
{
    public SchoolDayDetailPage()
    {
        InitializeComponent();
    }

    private void ChangeDate_Clicked(object sender, System.EventArgs e)
    {
        if (BindingContext is SchoolDayDetailViewModel vm && vm.SchoolDay.Value is SchoolDay sc)
        {
            var dialog = new DatePickerDialog(DependencyService.Get<Activity>(), async (s, e) =>
            {
                bool updateLessons = false;
                bool updateReports = false;
                if (sc.Lessons.Any())
                {
                    updateLessons = await DisplayAlert("日付を変更", "授業の日付も変更しますか？", "Yes", "No");
                }
                if (sc.Reports.Any())
                {
                    updateReports = await DisplayAlert("日付を変更", "レポートの日付も変更しますか？", "Yes", "No");
                }

                vm.UpdateDate(DateOnly.FromDateTime(e.Date), updateLessons, updateReports);
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
}
