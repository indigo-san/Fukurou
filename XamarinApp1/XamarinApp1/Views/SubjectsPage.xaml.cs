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

using XF.Material.Forms.UI.Dialogs;

namespace XamarinApp1.Views;

public partial class SubjectsPage : ContentPage
{
    public SubjectsPage()
    {
        InitializeComponent();
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
                await MaterialDialog.Instance.AlertAsync("教科の名前に空白を指定することはできません", "新しい教科");
            }
            else
            {
                vm.NewSubject.Execute(new Subject(name, Guid.NewGuid(), RandomColor.GetColor()));
            }
        }
    }
}
