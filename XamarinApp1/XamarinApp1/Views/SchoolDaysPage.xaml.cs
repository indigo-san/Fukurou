using Android.App;

using Com.Airbnb.Lottie.Model.Layer;

using System;
using System.Collections.Generic;
using System.Linq;

using Xamarin.Forms;
using Xamarin.Forms.Shapes;

using XamarinApp1.Models;
using XamarinApp1.Services;
using XamarinApp1.ViewModels;

using static Android.Graphics.ColorSpace;

namespace XamarinApp1.Views;

public class LessonsBehavior : Behavior<StackLayout>
{
    protected override void OnAttachedTo(StackLayout bindable)
    {
        base.OnAttachedTo(bindable);
        bindable.BindingContextChanged += Bindable_BindingContextChanged;
        if (bindable.BindingContext is IEnumerable<Lesson> items)
        {
            Update(bindable, items);
        }
    }

    protected override void OnDetachingFrom(StackLayout bindable)
    {
        base.OnDetachingFrom(bindable);
        bindable.BindingContextChanged -= Bindable_BindingContextChanged;
    }

    private static void Update(StackLayout stack, IEnumerable<Lesson> items)
    {
        stack.Children.Clear();
        foreach (var item in items)
        {
            var shape = new Ellipse()
            {
                WidthRequest = 24,
                HeightRequest = 24,
                Margin = new Thickness(0, 0),
                Fill = item.Subject?.Color ?? default
            };

            if (item.IsDuring)
            {
                shape.Stroke = Brush.White;
                shape.StrokeThickness = 2;
            }

            stack.Children.Add(shape);
        }
    }

    private void Bindable_BindingContextChanged(object sender, EventArgs e)
    {
        if (sender is StackLayout stack &&
            stack.BindingContext is IEnumerable<Lesson> items)
        {
            Update(stack, items);
        }
    }
}

public partial class SchoolDaysPage : ContentPage
{
    private bool isFirstLoading = true;

    public SchoolDaysPage()
    {
        InitializeComponent();
        ListView1.ItemAppearing += ListView1_ItemAppearing;
    }

    private void ListView1_ItemAppearing(object sender, ItemVisibilityEventArgs e)
    {
        if (isFirstLoading)
        {
            isFirstLoading = true;
            ScrollToToday_Clicked(null, EventArgs.Empty);
            ListView1.ItemAppearing -= ListView1_ItemAppearing;
        }
    }

    private void ListView_ItemSelected(object sender, SelectedItemChangedEventArgs e)
    {
        if (BindingContext is SchoolDaysViewModel vm && e.SelectedItem is SchoolDay sc)
        {
            ListView1.SelectedItem = null;
            vm.ItemTapped.Execute(sc);
        }
    }

    private void NewSchoolDay_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is SchoolDaysViewModel vm)
        {
            var dialog = new DatePickerDialog(DependencyService.Get<Activity>(), async (s, e) =>
            {
                await Shell.Current.GoToAsync(await vm.NewSchoolDay(DateOnly.FromDateTime(e.Date)));

            }, DateTime.Now.Year, DateTime.Now.Month - 1, DateTime.Now.Day);

            dialog.SetTitle("登校日を追加");
            dialog.Show();
        }
    }

    private async void ScrollToToday_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is SchoolDaysViewModel vm)
        {
            var today = DateOnly.FromDateTime(DateTime.Now);
            await vm.RefreshTask;

            for (int i = 0; i < vm.Items.Count - 1; i++)
            {
                var current = vm.Items[i];
                var next = vm.Items[i + 1];

                if (current.Date <= today && today <= next.Date)
                {
                    ListView1.ScrollTo(next, ScrollToPosition.Start, true);
                    return;
                }
            }
        }
    }
}
