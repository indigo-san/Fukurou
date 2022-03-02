using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace XamarinApp1.Views;

public partial class ReportFilterView : ContentView
{
    public ReportFilterView()
    {
        InitializeComponent();
        ReportStates.ItemsSource = new string[]
        {
            "未指定",
            "提出済み",
            "未提出",
        };
    }
}
