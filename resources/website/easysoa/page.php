<?php get_header(); ?>

<div id="left">
<?php if (have_posts()) : while (have_posts()) : the_post(); ?>

<h2><?php edit_post_link('[e]','<b>','</b> '); ?><?php the_title(); ?></h2>
<div class="contenttext">
<?php the_content(); ?>
</div>

<?php link_pages('<p><strong>Pages:</strong> ', '</p>', 'number'); ?>
<!--<?php comments_template(); ?>	-->
<?php endwhile; endif; ?>

</div>

<?php get_sidebar(); ?>
<?php get_footer(); ?>
