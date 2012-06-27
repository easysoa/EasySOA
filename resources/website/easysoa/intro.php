<?php
/*
Template Name: Welcome
*/
?>

<?php get_header(); ?>

<div id="left">

<?php if (have_posts()) : while (have_posts()) : the_post(); ?>
<div class="contenttext">
<?php the_content(); ?>
</div>
<?php endwhile; endif; ?>

<div id="welcomeLinks">
  <a href="http://www.easysoa.org/?page_id=52"><div id="welcomeLinkLearnMore">&raquo; Learn more</div></a>
  <a href="http://www.easysoa.org/?page_id=29"><div id="welcomeLinkDownload">&raquo; Download</div></a>
  <a href="http://www.easysoa.org/?page_id=33"><div id="welcomeLinkQuickStart">&raquo; Quick start</div></a>
</div>

<style type="text/css">
<!--
#welcomeLinks { height: 65px }
#welcomeLinkLearnMore { float: left; margin-left: 40px; width: 25%; text-align: center; font-size: 15px; background-color: #AAA; border: 1px solid #888; color: #333;
 border-radius: 8px; -moz-border-radius: 8px; -webkit-border-radius: 8px; }
#welcomeLinkLearnMore:hover { background-color: #CCC; }
#welcomeLinkDownload { float: left; margin-left: 10px; width: 25%; text-align: center; font-size: 15px; background-color: #8D8; border: 1px solid #6A6; color: #262;
 border-radius: 8px; -moz-border-radius: 8px; -webkit-border-radius: 8px; }
#welcomeLinkDownload:hover { background-color: #AEA; }
#welcomeLinkQuickStart { float: left; margin-left: 10px; width: 25%; text-align: center; font-size: 15px; background-color: #8AD; border: 1px solid #68A;  color: #246;
 border-radius: 8px; -moz-border-radius: 8px; -webkit-border-radius: 8px; }
#welcomeLinkQuickStart:hover { background-color: #ACE; }
-->
</style>

</div>

<?php get_sidebar(); ?>
<?php get_footer(); ?>
