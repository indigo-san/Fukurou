﻿using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Runtime.CompilerServices;

using Xamarin.Forms;

using XamarinApp1.Models;
using XamarinApp1.Services;

namespace XamarinApp1.ViewModels
{
    public class BaseViewModel : INotifyPropertyChanged
    {
        protected static IDataStore<SchoolDay> SchoolDayDataStore => DependencyService.Get<IDataStore<SchoolDay>>();

        protected static IDataStore<Report> ReportDataStore => DependencyService.Get<IDataStore<Report>>();

        protected static IDataStore<Lesson> LessonDataStore => DependencyService.Get<IDataStore<Lesson>>();
        
        protected static IDataStore<Subject> SubjectDataStore => DependencyService.Get<IDataStore<Subject>>();

        bool isBusy = false;
        public bool IsBusy
        {
            get { return isBusy; }
            set { SetProperty(ref isBusy, value); }
        }

        string title = string.Empty;

        public string Title
        {
            get { return title; }
            set { SetProperty(ref title, value); }
        }

        protected bool SetProperty<T>(ref T backingStore, T value,
            [CallerMemberName] string propertyName = "",
            Action onChanged = null)
        {
            if (EqualityComparer<T>.Default.Equals(backingStore, value))
                return false;

            backingStore = value;
            onChanged?.Invoke();
            OnPropertyChanged(propertyName);
            return true;
        }

        #region INotifyPropertyChanged
        public event PropertyChangedEventHandler PropertyChanged;
        protected void OnPropertyChanged([CallerMemberName] string propertyName = "")
        {
            var changed = PropertyChanged;
            if (changed == null)
                return;

            changed.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
        #endregion
    }
}
