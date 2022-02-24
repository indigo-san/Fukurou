using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using XamarinApp1.Models;
using XamarinApp1.Services;
using XamarinApp1.ViewModels;

namespace XamarinApp1.Views;

public partial class SubjectsPage : ContentPage
{
    public SubjectsPage()
    {
        InitializeComponent();
    }

    private async void Rename_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is SubjectsViewModel vm &&
            sender is MenuItem menuItem &&
            menuItem.BindingContext is Subject subject)
        {
            var name = await DisplayPromptAsync("名前を変更", "教科の新しい名前を入力", initialValue: subject.SubjectName);
            if (name != null)
            {
                vm.UpdateName.Execute((subject, name));
            }
        }
    }

    private void RandomColor_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is SubjectsViewModel vm &&
            sender is MenuItem menuItem &&
            menuItem.BindingContext is Subject subject)
        {
            vm.UpdateColor.Execute(subject);
        }
    }

    private async void Delete_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is SubjectsViewModel vm &&
            sender is MenuItem menuItem &&
            menuItem.BindingContext is Subject subject)
        {
            if (await DisplayAlert("教科を削除", $"'{subject.SubjectName}' を削除しますか？", "OK", "Cancel"))
            {
                vm.Delete.Execute(subject);
            }
        }
    }

    private async void NewSubject_Clicked(object sender, EventArgs e)
    {
        if (BindingContext is SubjectsViewModel vm)
        {
            var name = await DisplayPromptAsync("新しい教科", "新しい教科の名前を入力");
            if (name == null)
            {
                return;
            }
            else if (string.IsNullOrWhiteSpace(name))
            {
                await DisplayAlert("新しい教科", "教科の名前に空白を指定することはできません", "閉じる");
            }
            else
            {
                vm.NewSubject.Execute(new Subject(name, Guid.NewGuid(), RandomColor.GetColor()));
            }
        }
    }
}
