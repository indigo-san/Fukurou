
using Android.App;

using System;

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
            var dialog = new DatePickerDialog(DependencyService.Get<Activity>(), (s, e) =>
            {
                vm.UpdateDate(DateOnly.FromDateTime(e.Date));
            }, sc.Date.Year, sc.Date.Month - 1, sc.Date.Day);
            dialog.Show();
        }
    }
}
