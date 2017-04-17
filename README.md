Slide Up Pane
===========

## Introduction
This is a UI component that closely simulates the bottom UI component of the current Google Maps application, except bouncier.
It allows a panel to be placed at the bottom of the screen that a user can pull up over the view above it. Intermediate
stops can also be added so that it has "sticky" points along the path.  

## Installation
Make sure maven central is in your repository list then simply include the dependency.
```
dependencies {
    compile "com.briangriffey:slideuppane:1.0@aar"
}

```

## Usage
SlideUpPane can be added to any container.  It will slide over any other components placed in that container.  

For example:
```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context="com.example.example.MainActivity"
    tools:ignore="MergeRootFrame">

    <View
        android:layout_width="match_parent"
        android:layout_height="350dp"
        />

    <com.briangriffey.slideuppane.SlideUpPane
        android:id="@+id/pane"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@android:color/holo_orange_light">
          
           <Button
            android:text="some button"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Sint synth gastropub, assumenda shabby chic occaecat organic banjo cupidatat Odd Future voluptate Shoreditch YOLO. Bitters Echo Park pariatur aliqua quis, aute Brooklyn Intelligentsia umami. Meh quis Odd Future raw denim viral, literally sustainable hoodie freegan elit swag nostrud. Skateboard readymade deep v. Shabby chic freegan raw denim, ullamco semiotics before they sold out gentrify food truck adipisicing synth PBR cred consectetur artisan. Craft beer DIY occaecat, aliquip irony forage Godard placeat master cleanse Pitchfork sint small batch YOLO officia. Fashion axe labore YOLO id.

Odio roof party drinking vinegar wolf post-ironic. Id aliquip in, post-ironic occupy veniam fap organic readymade. Thundercats brunch plaid occaecat exercitation. Letterpress jean shorts tousled freegan, do tempor sartorial nisi Tumblr keytar banjo roof party aliquip bicycle rights. Ennui cred next level Godard id banh mi. Plaid Schlitz High Life literally 90's. Nihil reprehenderit hoodie, duis Tumblr qui craft beer.

Pickled single-origin coffee mumblecore yr eiusmod four loko, pour-over tote bag meggings asymmetrical whatever. Minim do shabby chic sunt. Deserunt Tumblr lomo, fugiat leggings YOLO eu Helvetica odio labore you probably haven't heard of them scenester kogi hoodie cornhole. Butcher elit plaid, ea eiusmod fashion axe aliquip incididunt ad commodo bitters skateboard hella next level. Laborum chambray jean shorts farm-to-table, eu XOXO cray Williamsburg McSweeney's brunch do kale chips. Ea commodo tempor, mustache aliqua sustainable lo-fi vinyl blog beard qui butcher. Gastropub Pitchfork excepteur, Schlitz raw denim non tofu polaroid laboris sapiente try-hard consectetur." />

    </com.briangriffey.slideuppane.SlideUpPane>


</LinearLayout>

```

The SlideUpPane extends a LinearLayout with a vertical orientation, so any components added to it will follow that convention.

## Demo
The video demos the pane with a 50% stop added
http://www.youtube.com/watch?v=FU4dkbuxNQ0


