## Intro
ImageComparator serves as a simplistic library to allow user to compare BufferedImages using multiple methods of comparison.


## Features

Image Comparator provides a robust set of tools for image analysis, including:
    Pixel-by-Pixel Comparison: Comparisons, with an option to skip pixels for faster processing. Produces output image with marked mismatched areas. Allows for exclusion of image areas that will not be compared.
    Hash Comparison: Utilize various hashing algorithms to detect image similarities, even after minor alterations:
        W-Hash
        A-Hash (Average Hash)
        P-Hash (Perceptual Hash)
        D-Hash (Difference Hash)
    SSIM (Structural Similarity Index Measure) Comparison: Assess the perceptual similarity between two images, offering a more human-like evaluation than simple pixel differences.


## How to Use

Image Comparator offers two primary ways to perform comparisons:
1. Comparator Classes. These classes provide a more complete comparison experience, allowing you to enforce or assert input image size equality.
   They return whether images are identical in accordance to default or user-set Config values.
    DirectComparator
    HashComparator
    SSIMComparator
   
3. Analyzer Classes. These allow user to get basic comparison scores/hashes/mismatches and to introduce custom comparison logic.
   The return values are usually indermediary values that can be compared by custom methods
    DirectAnalyzer
    AHashAnalyzer
    WHashAnalyzer
    PHashAnalyzer
    DHashAnalyzer
    SSIMAnalyzer

## Example:
note: DirectComparator is the most extensive comparator as this method allows for more granular and visual outcome.
```
DirectComparisonConfig = new DirectComparisonConfig()
    .colorSpace(ColorSpace.HSV)
    .colorDistanceThreshold(10);

DirectComparator comparator = new DirectComparator(config);

ExcludedAreas areasToExclude = new ExcludedAreas()
    .exclude(excludedRectangle);

DirectComparisonResult outcome = comparator.compare(image1, image2, areasToExclude);

outcome.getResultImage(); // Image with marked mismatched and excluded areas
outcome.getIsMatching();
```
