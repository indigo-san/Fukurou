using System;

using Xamarin.Forms;

namespace XamarinApp1.Services;

public static class RandomColor
{
    public static Color GetColor()
    {
        return Random.Shared.Next(0, 15) switch
        {
            // Red
            0 => Color.FromUint(0xffff1744),
            // Pink
            1 => Color.FromUint(0xfff50057),
            // Purple
            2 => Color.FromUint(0xffaa00ff),
            // Deep Purple
            3 => Color.FromUint(0xff6200ea),
            // Indigo
            4 => Color.FromUint(0xff304ffe),
            // Blue
            5 => Color.FromUint(0xff2962ff),
            // Light Blue
            6 => Color.FromUint(0xff0091ea),
            // Cyan
            7 => Color.FromUint(0xff00b8d4),
            // Teal
            8 => Color.FromUint(0xff00bfa5),
            // Green
            9 => Color.FromUint(0xff00c853),
            // Light Green
            10 => Color.FromUint(0xff64dd17),
            // Lime
            11 => Color.FromUint(0xffaeea00),
            // Yellow
            12 => Color.FromUint(0xffffd600),
            // Amber
            13 => Color.FromUint(0xffffab00),
            // Orange
            14 => Color.FromUint(0xffff6d00),
            // Deep Orange
            15 => Color.FromUint(0xffdd2c00),
            _ => throw new InvalidOperationException(),
        };
    }
}
