using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Platform.Android;
using Xamarin.Forms.Shapes;
using Xamarin.Forms.Xaml;

using XamarinApp1.Models;

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
                Fill = item.Subject.Color
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
    public SchoolDaysPage()
    {
        InitializeComponent();
    }
}
