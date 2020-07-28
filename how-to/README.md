<h1 style="font-size:15vmin">How I write single-file java libraries</h1>
<h2>Doeke Wartena, 2020</h2>
<h6>last revision 28 of July</h6>

<br>

**Table of Contents**

- [How I write single-file java libraries](#how-i-write-single-file-java-libraries)
  - [Some Java frustrations](#some-java-frustrations)
- [How I write them, part II](#how-i-write-them-part-ii)
    - [package name](#package-name)
    - [class declaration](#class-declaration)
    - [list out the data](#list-out-the-data)
    - [dealing with access](#dealing-with-access)
    - [constructor alternative](#constructor-alternative)
    - [function style over methods style](#function-style-over-methods-style)
    - [using a single-file library](#using-a-single-file-library)
  - [generating documentation](#generating-documentation)
  - [Syntax](#syntax)
    - [CamelCase vs snake_case](#camelcase-vs-snake_case)
    - [`static public` vs `public static` order](#static-public-vs-public-static-order)

---

# How I write single-file java libraries

The way I write single-file java libraries and java in general goes against almost every java convention. I have lived by the java conventions for years cause I thought they where important. Over time I learned java is not that good of a language (like most languages unfortunately). I will mostly point out reasons that have a relation to how I write single-file libraries. If I would list all frustrations then this doucment would become an infinite scroll page.

---
## Some Java frustrations

- Every class should live in it's own file. This is terrible, for one, the human brain is really limited. We are way better in understanding things we can read from top to bottom. With every class in it's own file there is way more digging around in files to get a understanding of what is going on. Sure a top-to-bottom aproach is never really possible when dealing with code, but it can be attempted. Every class in it's own file is like tearing out the chapters of a book and laying them out in alphabetic order.

- The folder structure represent the package name. This is not a huge frustration cause the time dealing with this is minor. Also it is understandable as one approach from the perspective of avoiding naming conflicts, but why not use something like namespaces like in *C++*. The problem also does not lies in the java file for me but on the hard disk it self.
The code itself is too many levels deep in the folder structure.

    ![](images/package_hiearchy_finder.png "Hiearchy in Finder")

    It adds a lot of navigation steps when using *Finder* or *Windows Explorer* or any other file browser. And it is a waste of vertical space.

    Compliments to [VSCode](https://code.visualstudio.com/):

    ![](images/package_hiearchy_vscode.png "Hiearchy in VSCode")

- Java is suffering from the OOP paradigm. This is a big topic and would take me several alineas to attack correctly. To keep it really short, the main purpose of a computer is to manipulate data, so any program should be centered around that purpose. OOP does not do that since it is about **Programming Oriented around Objects**, hence the name OOP. It is a nice concept in theory but not so in practise, it does not scale well with increasing complexity of projects, instead it is a way of putting up walls. OOP is harder to read, debug and maintain. Wen I say OOP I don' mean so much about methods living inside a class, but more about subjects like inheritance of mutliple levels deep like:

```java
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable { .. }

public interface List<E> extends Collection<E> { .. }

public interface Collection<E> extends Iterable<E> { .. }

public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> { .. }

public abstract class AbstractCollection<E> implements Collection<E> { .. }
```

- Access modifiers are terrible. To many times I have needed access to a field that was `private` or `protected`. Now in order to do this you have to use reflection, this adds a lot of friction by the amount of lines required to acces a field. It's a waste of CPU cycles, and worse of all now your program relies on a string to retrieve the data. So instead of using:

    ```java
    foo.x = 10;
    ```

    Now to change the value of `x`:

    ```java
    try {
        Field x_field = Foo.class.getDeclaredField("x");
        x_field.setAccessible(true);
        x_field.set(foo, 666);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
    }
    ```

    Can it be worse? Yes it can!

    Let's say the creator of `foo.x` decides to rename `x` to `x_pos`, since your own program still relies on the string `"x"` it will compile perfectly fine. And the benefits of a having a staticly typed language are out the window. You will get a `NoSuchFieldException` at runtime, that is if you hit it. It might be a low probabilty case that does not trigger when you test.

    Personally I think java should have gone with a syntax like this (that is if they really want to go for restricted access options (which they do)):

    `foo.(private)x = 10;`

    Where there is a compile time error when the access cast does not match the actual access modifier. But suggestions to make Java better is like having suggestions to prevent WWII.

---
# How I write them, part II

> **&#x26A0;** How I write them is an ongoing process, but I feel like I'm close to where I finally will end of how I write them.

>**&#x26A0;** On large projects I still use multiple files, but each individual file follows the style of how I write a single file library.

>**&#x26A0;** To almost everything I explain below I will have exceptions in certain cases. For clarity of the document I won't point out most of those exceptions. Just remember that this document is meant as a guideline and not as rules written in stone.

---
### package name

```java
package just_use_underscores;
```

Results in:

![](images/just_use_underscores.png "With underscores")

compared to:

```java
package just.use.underscores;
```

![](images/just_use_underscores_DIDNT.png "Without underscores")


<br>

> **&#x26A0;** I still have nested folders inside the `src` directory, but I keep the depth to a reasonable minimum.

---
### class declaration

```java
public class Acts_as_Namespace {
     private Acts_as_Namespace(){}
```

The class that is matching the filename just works as a container to hold all the other inner classes. There should **never** be an instance created of this class. Therefor the `private` acces modifier is used for the constructor. This is one of the few times I use anything other then `public`. I like to align them which tends to work as a more clear visual indicator that this is the "namespace" part.

> **&#x26A0;** After the constructor, everything in the file has one indentation less then usual. I like to keep the code close to the left border of the editor. It **slighly** adds to the clariness when scrolling threw the file. Also I "win" 4 spaces of space, which in some cases are very welcome.

---
### list out the data

Every field and function have to be `static` from this point on, since we cannot create an instance of `Acts_as_Namespace`!.

```java
//
// globals:
//
static public final float MINIMAL_USE_OF_GLOBALS = 1.23;
static public       float all_globals_have_to_be_constant = 45.6;
```

Next I layout all data, inspired by `struct`'s of `C`:

```java
//
// data:
//
static public class Simulair_To_Structs { 
    public float every_thing_is_public;
}

static public class All_Nested_Classes_Static { 
    public float _underscore_instead_of_private;
}

static public class Vec2 { 
    public float x;
    public float y;
    public Vec2(float x, float y) {this.x = x; this.y = y;}
}

static public class Pulate {
    public String ditzparts;
    private Pulate();
}

static public class Atinitz {
    public String   izzillent;
    public float    hamletive;
    public float    _partraully;
    public boolean  strizzes;
    public Pulate   pulate = make_pulate("eminste");
    public short[]  eptions;
    public boolean  monalled = true;
    public int      skizze = -1;
    private Atinitz();
}
```

The main goal here is density of the data, that is in vertical editor space. Reading those classes often already gives an insight in what the program does and how it is structured. If there are no circulair dependencies, then I declare in top-to-bottom order. So i class `B` is using class `A` then I prefer to declare class `A` first.
About *>95%* of the time I don't have constructors. Sometimes I add a single-line `private` constructor to prevent the user of creating an instance using the `new` keyword. Forcing them to make use of a function that makes them, more about that later. When the constructor can be on a single line without getting to obscure, then I tend to do that, see the `Vec2` example.

---
### dealing with access 

Make **everything** `public`, if something is not meant to be accessed from outside the library then just prefix it with a `_`. See for example `_underscore_instead_of_private` in the `All_Nested_Classes_Static` class. I use this really rarely, thinking about protection levels is brainpower spent on something that could have been used on something more important.

It might be tempting to think:
> Someone will never need this, I can defenitly make this `private`!

Just don't, you might shoot someone (else) in the foot with that someday.

---
### constructor alternative

I just make simple functions that are prefixed with "*make_*". One other adventage is that now `null` can be returned. Where using a constructor always yields in an instance.

```java
//
// make:
//
static public Pulate make_pulate(String ditzparts) {
    Pulate r = new Pulate();
    r.ditzparts = ditzparts;
    return r;
}

static public Atinitz make_atinitz(String izzillent, boolean  strizzes) {
    Atinitz b = new Atinitz();
    b.izzillent = izzillent;
    b.strizzes = strizzes;
    return b;
}
```

But since java has no named parameters, making both constructors and "*make_*" functions obscure, I tend to create the instances in the place where I need them and setting the fields there. In general I only reccomend doing this if you create the instances in one place.

```java
// inside some function
Atinitz a = new Atinitz();
a.izzillen   = "gravene";
a.hamletive  = minimal_use_of_globals < 0 ? minimal_use_of_globals : 1.248f;
a.strizzes   = b.strizzes;
```

If you do have to create instances in multipe places, then I reccomend naming the parameters before calling a `make_*` function. This is to avoid calls with parameters like: `(null, 1, 7, true, false, false, false, true, null);`


---
### function style over methods style

Instead of having methods inside the classes:

```java
Pulate p  = ..;
Atinitz a = ..;

p.yuneticketrims(a);
// or
a.yuneticketrims(p);
```

Go for a more functional approach like this:

```java
static public void yuneticketrims(Pulate p, Atinitz a) { .. }

// somewhere else:
Pulate p  = ..;
Atinitz a = ..;
yuneticketrims(p, a);
```

This greatly simplifies things in the mind, I have been amazed by this many times.
Another huge plus is that it reduces the risk of creating bugs. To give an example:

Let's say you went for the OOP way and both `Bar` and `Foo` have `craziness(float x)` method and you want to call that method from inside `Foo` like this:
```java
bar.craziness(1.23f);
```
But you accidently typed this:
```java
craziness(1.23f);
```
Since the method `craziness` exists inside `Foo` you compile perfectly fine, but now you have a bug that can be really hard to detect! Specially if you don't crash but get undefined behaviour.

---
### using a single-file library

There is not much to it, it does make life easy to import them with `static`.

```java
import static just_use_underscores.Acts_as_Namespace.*;
```
This allows for ommiting the containing class name so you can make calls like `make_pulate(..)` instead of `Acts_as_Namespace.make_pulate(..)`. However, in some cases making a function call in a non static way can be better for clarity.

> **&#x26A0;** Sometimes when there are name conflicts you have to do a non static import aswell and make calls like `Acts_as_Namespace.noise(..)`.

---
## generating documentation

I'm still figuring out a good way for doing this. I do feel that inside the source code itself is not a good place to have the documentation. No matter if it got there by generation or not. I do have ideas of what could be a good way for documentation with safety checks incase the documentations outdates the source. If I have this ready and tested then I will update this document.

---
## Syntax

The scope of this article is not on syntax, however I still wanted to give it some brief attention.

---
### CamelCase vs snake_case 

I prefer snake_case over CamelCase cause it gives more breathing room, for class names I use Camel_Snake_Case. I was suprised most people prefer CamelCase over snake_case.

> ![](images/CamelCase_vs_snake_case.png)

https://poll.fm/4528769/

I was more suprised by some of the recuring arguments I see online, e.g. that CamelCase is easier to type. First off all, the time we spend reading code is magnitudes larger then the time spend on writing code. Also unless your on a mobile phone, I don't see how `_` is hard to type. Another argument I saw recuring is that it is shorter, 

Here is some scientific research from 2009 where to my suprise CamelCase won in readability.

[To CamelCase or Under_score](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.158.9499&rep=rep1&type=pdf)

And here is a scientific research from 2010 using an eye tracker where snake_case came out better.

[An Eye Tracking Study on camelCase and under_score Identifier Styles](http://www.cs.kent.edu/~jmaletic/papers/ICPC2010-CamelCaseUnderScoreClouds.pdf)

Anyway, in the end it comes down to a personal liking, or the conventions you have to follow by the company you work for.

---
### `static public` vs `public static` order

By convention the order is `public static`, but I'm more interested if something is `static` or not. Same goes for `final`, in general I put `public` at the end.
Except in classes cause I like when things align neatly e.g.:

```java
public final boolean debug;
public UI_State state = new UI_State(true);
public UI_State prev_state = new UI_State(false);
public UI_Builder ui_builder = new UI_Builder();
public Element_Node root_node = make_element_node(null, "hidden_root", -1);
public transient Comparator<Element> draw_buffer_comparator;
```
