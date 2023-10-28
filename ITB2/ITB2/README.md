# ImageToolBox²

The ImageToolBox² lets users write filter classes, which can accept images, do some
calculations on them and produce new images. Using the ITB², those filters can be
opened and executed. Filters may provide properties that can be changed inside the ITB².

## Basic concept
### Image
An image has multiple channels, a width and a height. The coordinate origin is in the
top left corner; the rows are counting top to bottom, and the columns are counting left
to right. There are a few basic image types, but users can create there own types, if
they provide filter for converting images from basic types to their own type.

The basic image types are:
* RGB-Image
* HSI-Image
* HSV-Image
* Grayscale-Image
* Binary-Image
* Grouped-Image
* "Drawable"-Image

### Automatic Image Conversion
To make the use of different image types easier, ITB² supplies an automatic image
conversion system: A filter may add an `@RequireImageType` annotation; once the
filter is called, all incoming images will automatically be converted into the required
image type. There are basic image conversions implemented, but any filter can
register itself as a converter using `ImageConverter.register(...)`.

If a filter needs to convert an image to another type at runtime, it can use
`ImageConverter.convert(...)`.

### Filter
The basic filter only needs to implement the `Filter` interface, and thus having two
functions: One to return a collection of `FilterProperty` and the other to execute on an array
of images and returning an array of filtered images.  
To make the life even easier, there is an `AbstractFilter` that gives the basic
functionality:

**AbstractFilter**  
The `AbstractFilter` gives two options for implementation:
* `filter(Image input):Image`
* `filter(Image[] input):Image[]`

In most cases the first is sufficient, the second method may only be used, if the filter
requires multiple images *(e.g. difference between two images)* or returns multiple images
*(e.g. image pyramid)*.  
Filter can register properties in there constructor using `properties.add...`
and later read the value using `properties.get...`. The property name serves as an ID.  

### Controller
The controller gives the filter access to some basic functionality of the ITB². Most
important is the `CommunicationManager`. Using it, the filter can send messages to
the user as well as give an update of the current progress; this is especially useful if
the filter takes a bit longer.

## Image types
**RGB-Image**  
The most basic image type, containing three channels for red, green and blue.

**HSI-Image**  
Image containing three channels for hue, saturation and intensity. The last channel is
equivalent to a grayscale-image.

**HSV-Image**  
Image containing three channels for hue, saturation and value.

**Grayscale-Image**  
Image containing only one channel for intensity.

**Binary-Image**  
Each pixel can only be 0 or 1. Similar to a grouped-image with two groups.

**Grouped-Image**  
Each pixel is assigned to one group. Pixel of the same group will be colored the same.

**"Drawable"-Image**  
Wraps a BufferedImage and gives the option to draw on a Graphics object.
