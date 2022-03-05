using Reactive.Bindings;

using System;
using System.Text.Json.Serialization;

using Xamarin.Forms;

namespace XamarinApp1.Models;

public record Subject(string SubjectName, Guid Id, Color Color, int RequiredAttendance = -1);

public record Lesson(Subject Subject, DateOnly Date, TimeOnly Start, TimeOnly End, Guid Id, LessonState State = LessonState.None, bool IsArchived = false)
{
    public string Room { get; init; } = string.Empty;

    public string Memo { get; init; } = string.Empty;

    [JsonIgnore]
    public string DateHeader
    {
        get
        {
            var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
            if (nowDateOnly == Date)
            {
                return "本日";
            }
            else if (nowDateOnly < Date)
            {
                return (Date.DayNumber - nowDateOnly.DayNumber).ToString();
            }
            else if (nowDateOnly > Date)
            {
                return (nowDateOnly.DayNumber - Date.DayNumber).ToString();
            }
            else
            {
                return "";
            }
        }
    }

    [JsonIgnore]
    public string DateHeaderSuffix
    {
        get
        {
            var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
            if (nowDateOnly == Date)
            {
                return "";
            }
            else if (nowDateOnly < Date)
            {
                return "日後";
            }
            else if (nowDateOnly > Date)
            {
                return "日前";
            }
            else
            {
                return "";
            }
        }
    }

    [JsonIgnore]
    public bool IsCompleted
    {
        get
        {
            var now = TimeOnly.FromDateTime(DateTime.Now);
            var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
            return End < now && Date <= nowDateOnly;
        }
    }

    [JsonIgnore]
    public bool IsDuring
    {
        get
        {
            var now = TimeOnly.FromDateTime(DateTime.Now);
            var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
            return Date == nowDateOnly &&
                Start < now &&
                now < End;
        }
    }
}

public enum LessonState
{
    None,
    Attend,
    Absent
}

public enum ReportState
{
    Submitted,
    NotSubmitted
}

public record Report(Subject Subject, DateOnly Date, string Name, Guid Id, ReportState State, bool IsArchived = false)
{
    [JsonIgnore]
    public bool IsSubmitted => State == ReportState.Submitted;

    [JsonIgnore]
    public bool IsNotSubmitted => State == ReportState.NotSubmitted;

    [JsonIgnore]
    public bool IsExpirationOfTerm => IsNotSubmitted && Date < DateOnly.FromDateTime(DateTime.Now);
}

public record SchoolDay(DateOnly Date, Guid Id)
{
    [JsonIgnore]
    public string DateHeader
    {
        get
        {
            var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
            if (nowDateOnly == Date)
            {
                return "本日";
            }
            else if (nowDateOnly < Date)
            {
                return (Date.DayNumber - nowDateOnly.DayNumber).ToString();
            }
            else if (nowDateOnly > Date)
            {
                return (nowDateOnly.DayNumber - Date.DayNumber).ToString();
            }
            else
            {
                return "";
            }
        }
    }

    [JsonIgnore]
    public string DateHeaderSuffix
    {
        get
        {
            var nowDateOnly = DateOnly.FromDateTime(DateTime.Now);
            if (nowDateOnly == Date)
            {
                return "";
            }
            else if (nowDateOnly < Date)
            {
                return "日後";
            }
            else if (nowDateOnly > Date)
            {
                return "日前";
            }
            else
            {
                return "";
            }
        }
    }

    [JsonIgnore]
    public Lesson[] Lessons { get; init; } = Array.Empty<Lesson>();

    [JsonIgnore]
    public Report[] Reports { get; init; } = Array.Empty<Report>();
}
