using Android.App;

using System;
using System.Collections.Generic;

using Xamarin.Forms;
using Xamarin.Forms.Shapes;

using XamarinApp1.Models;
using XamarinApp1.ViewModels;

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
        CollectionView1.SizeChanged += CollectionView1_SizeChanged;
    }

    private void CollectionView1_SizeChanged(object sender, EventArgs e)
    {
        if (isFirstLoading)
        {
            isFirstLoading = false;
            ScrollToToday_Clicked(null, EventArgs.Empty);
            CollectionView1.SizeChanged -= CollectionView1_SizeChanged;
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

    private async void ScrollToDate(DateOnly date)
    {
        if (BindingContext is not SchoolDaysViewModel vm) return;

        await vm.RefreshTask;
        if (vm.Items.Count >= 1)
        {
            var first = vm.Items[0];
            var last = vm.Items[^1];
            if (first.Item.Date >= date)
            {
                CollectionView1.ScrollTo(first, null, ScrollToPosition.Start);
            }
            else if (last.Item.Date <= date)
            {
                CollectionView1.ScrollTo(last, null, ScrollToPosition.Start);
            }
            else
            {
                for (int i = 0; i < vm.Items.Count - 1; i++)
                {
                    var current = vm.Items[i];
                    var next = vm.Items[i + 1];

                    if (current.Item.Date <= date && date <= next.Item.Date)
                    {
                        CollectionView1.ScrollTo(next, null, ScrollToPosition.Start);
                        return;
                    }
                }
            }
        }
    }

    private void ScrollToToday_Clicked(object sender, EventArgs e)
    {
        ScrollToDate(DateOnly.FromDateTime(DateTime.Now));
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
