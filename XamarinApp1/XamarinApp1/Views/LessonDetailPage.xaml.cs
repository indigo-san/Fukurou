using Android.App;

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using XamarinApp1.Models;
using XamarinApp1.ViewModels;

namespace XamarinApp1.Views;
[XamlCompilation(XamlCompilationOptions.Compile)]
public partial class LessonDetailPage : ContentPage
{
    public LessonDetailPage()
    {
        InitializeComponent();
    }

    private void ChangeDate_Tapped(object sender, EventArgs e)
    {
        if (BindingContext is LessonDetailViewModel vm && vm.Lesson.Value is Lesson lesson)
        {
            var dialog = new DatePickerDialog(DependencyService.Get<Activity>(), (s, e) =>
            {
                vm.UpdateDate(lesson, DateOnly.FromDateTime(e.Date));
            }, lesson.Date.Year, lesson.Date.Month - 1, lesson.Date.Day);
            dialog.Show();
        }
    }
}
