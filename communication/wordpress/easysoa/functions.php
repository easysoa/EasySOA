<?php

if ( function_exists('register_sidebar') ) {

  register_sidebar(array(
    'name' => 'Right menu',
    'before_widget' => '<div id="%1$s" class="%2$s">',
    'after_widget' => '</div>',
    'before_title' => '<h2>',
    'after_title' => '</h2>'
  ));
  
  register_sidebar(array(
    'name' => 'Bottom menu',
    'before_widget' => '<div>',
    'after_widget' => '</div>',
    'before_title' => '<h3>',
    'after_title' => '</h3>'
  ));
  
  register_sidebar(array(
    'name' => 'Top right space',
    'before_widget' => '<div class="topWidget">',
    'after_widget' => '</div>'
  ));

}

function register_my_menus() {
  register_nav_menus(
    array('header-menu' => __( 'Header Menu' ) )
  );
}

add_action( 'init', 'register_my_menus' );

?>
