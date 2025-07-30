using System.Drawing;
using AppCore;

namespace Filters.WarmColorsFilter;

public class WarmFilter : IFilter
{
    public Bitmap Apply(Bitmap inputImage)
    {
        var warmImg = new Bitmap(inputImage.Width, inputImage.Height);
        
        for (int y = 0; y < inputImage.Height; y++)
        {
            for (int x = 0; x < inputImage.Width; x++)
            {
                var pixel = inputImage.GetPixel(x, y);
                int newRed = Math.Min(255, (int)(pixel.R * 1.3));
                int newGreen = Math.Min(255, (int)(pixel.G * 1.1));
                int newBlue = Math.Max(0, (int)(pixel.B * 0.7));
                var warmColor = Color.FromArgb(pixel.A, newRed, newGreen, newBlue);
                warmImg.SetPixel(x, y, warmColor);
            }
        }
        return warmImg;
    }
}