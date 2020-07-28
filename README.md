# sfjl
Collection of single-file java libraries with minimal to no dependencies, inspired by the [stb](https://github.com/nothings/stb) libraries.

# Libraries

| library                                       | latest version | category | LoC | description                                      |
| --------------------------------------------- | -------------- | -------- | --- | ------------------------------------------------ |
| **[SFJL_Print][sfjl_print_link]**             |                | utility  | 175 | print and println without the need of System.out |
| **[SFJL_Base64][sfjl_base64_link]**           |                | utility  | 136 | base64 encoder and decoder functions             |
| **[SFJL_FNV_Hash][sfjl_fnv_hash_link]**       |                | utility  | 148 | [FNV hash][fnv_link] implementation              |
| **[SFJL_Tween][sfjl_tween_link]**             |                | graphics | 187 | tween functions                                  |
| **[SFJL_Douglas_Peucker][sfjl_douglas_link]** |                | graphics | 179 | line based curve decimator for 2d and 3d         |
|                                               |                | **sum**  | 825 |                                                  |


[sfjl_tween_link]: src/sfjl/SFJL_Tween.java
[sfjl_douglas_link]: src/sfjl/SFJL_Douglas_Peucker.java
[sfjl_print_link]: src/sfjl/SFJL_Print.java
[sfjl_base64_link]: src/sfjl/SFJL_Base64.java
[sfjl_fnv_hash_link]: src/sfjl/SFJL_FNV_Hash.java
[fnv_link]: http://www.isthe.com/chongo/tech/comp/fnv/

# Dependencies


## Dependencies Libraries
| library                                                        | dependencies |
| -------------------------------------------------------------- | :----------- |
| **[SFJL_Print](src/sfjl/SFJL_Print.java)**                     | &#x274c;     |
| **[SFJL_Base64](src/sfjl/SFJL_Base64.java)**                   | &#x274c;     |
| **[SFJL_FNV_Hash](src/sfjl/SFJL_FNV_Hash.java)**               | &#x274c;     |
| **[SFJL_Douglas_Peucker](src/sfjl/SFJL_Douglas_Peucker.java)** | &#x274c;     |
| **[SFJL_Tween](src/sfjl/SFJL_Tween.java)**                     | &#x274c;     |

## Dependencies Libraries Examples

> &#x26A0; The dependencies of the library itself are not repeated here

> &#x26A0; The dependencies are included in the repository

| example                                                       | dependencies                     |
| ------------------------------------------------------------- | :------------------------------- |
| **[SFJL_Print_Example][sfjl_print_example_link]**             | &#x274c;                         |
| **[SFJL_Base64_Example][sfjl_base64_example_link]**           | &#x274c;                         |
| **[SFJL_FNV_Hash_Example][sfjl_fnv_hash_example_link]**       | &#x274c;                         |
| **[SFJL_Douglas_Peucker_Example][sfjl_douglas_example_link]** | [processing](www.processing.org) |
| **[SFJL_Tween_Example][sfjl_tween_example_link]**             | [processing](www.processing.org) |

[sfjl_tween_example_link]: src/sfjl_examples/SFJL_Tween_Example.java
[sfjl_print_example_link]: src/sfjl_examples/SFJL_Print_Example.java
[sfjl_base64_example_link]: src/sfjl_examples/SFJL_Base64_Example.java
[sfjl_fnv_hash_example_link]: src/sfjl_examples/SFJL_FNV_Hash_Example.java
[sfjl_douglas_example_link]: src/sfjl_examples/SFJL_Douglas_Peucker_Example.java

# Creating jar files

I have not made up my mind about how I want to provide this functionality. Right now i'm waiting for [support exporting jars][support_exporting_jars] in VSCode, which is planned to be released within a week (today is 24 July, 2020). I know it's bad to rely on a specific editor to provide such functionality. However I prefer that over using Maven or Gradle. I used shell scripts before, but they did not support windows.

[support_exporting_jars]: https://github.com/microsoft/vscode-java-dependency/pull/271/files/57a8fd0700eefef1c9317d81720cdcc814a931e8..69277f4347b0720618f45a3056cd0a938ca7f511


# Soon to come libraries
More libraries are coming soon, i'm in the process of wrapping them up.
Here a list of what is coming for sure:
| library               | category | description                                        |
| --------------------- | -------- | -------------------------------------------------- |
| SFJL_Blobscanner      | graphics | blobscanner                                        |
| SFJL_Quad_Tree        | graphics | quad_tree with lots of advanced retreive functions |
| SFJL_Spatial_Hash_Map | graphics | data structure to store points based on hashes     |



# FAQ
#### Any advice of how to write single-file libraries for java?
Yes, [How I write single-file java libraries](how-to/README.md)

#### Are there other single-file java libraries with public-domain/open source libraries with minimal dependencies out there?
I can't find them, if you know any please let me know. I would love to list them.

<br>

*Special thanks to [Sean Barrett](http://nothings.org/) for being a huge inspiration for this project*

