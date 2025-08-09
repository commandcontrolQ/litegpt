# LiteGPT

> [!WARNING]
> This applet is currently not considered stable and contains known issues.

LiteGPT is a Java applet that allows users to interface with the ChatGPT artificial intelligence on hardware that does not support the official website.
It is based off of the HTTP portion of the ch.at project, which can also be accessed via SSH, cURL, or DNS (via the dig command).
For more information about the ch.at project, see https://github.com/Deep-ai-inc/ch.at

## Compiling
These instructions assume that you are compiling using Java SE 8 (since the JDK is easier to obtain)

1. Clone this repository either using the `git` command or via downloading a zip file from the repository: <br>
`git clone https://github.com/commandcontrolQ/litegpt`

2. Enter the litegpt folder: <br>
`cd litegpt`

3. Compile the project (this will generate the necessary classes): <br>
`path\to\jdk\bin\javac -source 6 -target 6 -bootclasspath path\to\jdk\jre\lib\rt.jar com\litegpt\*.java`

4. Create the Java archive to compress everything needed into one file: <br>
`path\to\jdk\bin\jar cvfe LiteGPT.jar com.litegpt.Main com/litegpt/*.class com/litegpt/*.wav com/litegpt/splash.png`

You should now have a single jar file called LiteGPT.jar (which you can run using `java -jar LiteGPT.jar`).

## Prerequisites and System Requirements

You will need at least:
- Java SE 6 (and by definition an operating system that supports Java SE 6)
- A stable internet connection

The following operating systems support Java SE 6:
- Windows 2000 and newer (Windows XP is recommended)
- Mac OS X Snow Leopard and newer (Leopard requires a 64-bit processor)
- Red Hat Linux 9, Debian 3.1, Ubuntu 6.06 LTS, Slackware 10.0 or any other similar Linux distribution and newer

## Notes and Issues

There are a few quirks and issues with the current implementation of LiteGPT:
- Due to the way the AI is queried, there is no form of memory. **This is a known quirk and will not be fixed.**
- LaTeX formatting is not supported. **This is a known issue but will not be fixed.**
- URLs can only be clicked and opened if they are embedded in Markdown. **This is a known issue and a fix is currently being worked on.**
