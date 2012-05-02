<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <script type="text/javascript" src="<?php bloginfo('template_url'); ?>/js/dropdown.js"></script>
  <meta http-equiv="content-type" content="text/html; charset=<?php bloginfo('charset'); ?>" />
  <meta name="description" content="<?php bloginfo('name'); ?> - <?php bloginfo('description'); ?>" />
  <meta name="keywords" content="" />
  <link rel="stylesheet" type="text/css" href="<?php bloginfo('stylesheet_url'); ?>" />
  <link rel="alternate" type="application/rss+xml" title="RSS 2.0" href="<?php bloginfo('rss2_url'); ?>" />
  <link rel="alternate" type="text/xml" title="RSS .92" href="<?php bloginfo('rss_url'); ?>" />
  <link rel="alternate" type="application/atom+xml" title="Atom 0.3" href="<?php bloginfo('atom_url'); ?>" />
  <link rel="icon" type="image/png" href="/favicon.png" />
  <!--[if IE]><link rel="shortcut icon" type="image/x-icon" href="/favicon.ico" /><![endif]-->
  <link rel="pingback" href="<?php bloginfo('pingback_url'); ?>" />
  <title><?php bloginfo('name'); wp_title(); ?></title>
  <?php wp_head(); ?>

<!-- Google Analytics -->
<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-25807801-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>


</head>


<body>
  <div id="top">
    <div id="topWidgets">
	<?php if ( function_exists('dynamic_sidebar') ) dynamic_sidebar('Top right space') ?>
    </div>
    <div id="topMenuWrap">
    <div id="topMenu">
      <div id="topLogo">
        <a href="<?php bloginfo('url'); ?>"><img src="<?php bloginfo('template_url'); ?>/img/EasySOA-50px.png" /></a>
      </div>
      <div id="topLinks">
        <?php
        
        
          function render_item($item, $next_item, $dropdownid) {
             if ($item->menu_item_parent != 0) {
                  echo '<li><a class="underline" href="'.$item->url.'">'.$item->title.'</a></li>';
                  if ($next_item == null || $next_item->menu_item_parent == 0) {
                    echo '</ul>';
                    echo '</dd>';
                    echo '</dl>';
                  }
              }
              else {
                if ($next_item == null || $next_item->menu_item_parent == 0) {
                  echo '<a class="link" href="'.$item->url.'">'.$item->title.'</a>';
                }
                else {
                  echo '<dl class="dropdown">';
                  echo '<dt onmouseout="ddMenu(\'dd'.$dropdownid.'\',-1)" onmouseover="ddMenu(\'dd'.$dropdownid.'\',1)" id="dd'.$dropdownid.'-ddheader">'.$item->title.'</dt>';
                  echo '<dd onmouseout="ddMenu(\'dd'.$dropdownid.'\',-1)" onmouseover="cancelHide(\'dd'.$dropdownid.'\')" id="dd'.$dropdownid.'-ddcontent" style="display: block; height: 4px; opacity: 0.0294118;">';
                  echo '<ul>';
                  $dropdownid++;
                }
              }
          } 
        
          $locations = get_nav_menu_locations();
          $items = wp_get_nav_menu_items($locations['header-menu'], $args = array());
          $previousitem = null;
          $dropdownid = 0;
          
          foreach ($items as $item) {
            if ($previousitem != null) {
             render_item($previousitem, $item, $dropdownid++);
            }
            $previousitem = $item;
          }
          render_item($previousitem, null, $dropdownid);
        
         ?>
      </div>
    </div>
    </div>
  </div>
  <div id="center">
    <div id="content">