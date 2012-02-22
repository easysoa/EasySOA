<div id="right">

<?php if ( function_exists('dynamic_sidebar') && dynamic_sidebar('Right menu') ) : else : ?>

<h2>Pages</h2>
<ul>
<li><a href="<?php bloginfo('home'); ?>">Home</a></li>
<?php wp_list_pages('sort_column=menu_order&title_li=') ?>
</ul>

<h2>Categories</h2>
<ul>
<?php wp_list_cats('sort_column=name&optioncount=1&hierarchical=1&children=1&hide_empty=1'); ?>
</ul>

<?php if ( function_exists('wp_tag_cloud') ) : ?>
<h2>Popular tags</h2>
<p>
<?php wp_tag_cloud('unit=em&smallest=0.8&largest=2'); ?>
</p>
<?php endif; ?>

<?php /*
<?php get_calendar(1); ?>

<h2>Archive</h2>
<ul>
<?php wp_get_archives('type=monthly&show_post_count=1'); ?>
</ul>
*/ ?>

<?php include (TEMPLATEPATH . '/searchform.php'); ?>

<?php endif; ?>
<?php wp_meta(); ?>
</div>
