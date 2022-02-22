using Reactive.Bindings;

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace XamarinApp1.ViewModels;

public class DiscoverViewModel : BaseViewModel
{
    public DiscoverViewModel()
    {

    }

    public ReactivePropertySlim<bool> IsNextLessonVisible { get; }
}
