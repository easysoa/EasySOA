<?php get_header(); ?>

<div id="left">
<?php if (have_posts()) : ?>
<?php $post = $posts[0]; ?>

<?php if (is_category()) { ?><h2>Archive for category: <?php echo single_cat_title(); ?></h2>
<div class="introtext"><?php echo category_description(); ?></div>
<?php } elseif (is_day()) { ?><h2>Archive for date: <?php the_time('F jS, Y'); ?></h2>
<?php } elseif (is_month()) { ?><h2>Archive for month: <?php the_time('F, Y'); ?></h2>
<?php } elseif (is_year()) { ?><h2>Archive for year: <?php the_time('Y'); ?></h2>
<?php } elseif (is_tag()) { ?><h2><?php single_tag_title('Archive for tag: '); ?></h2>
<?php } elseif (isset($_GET['paged']) && !empty($_GET['paged'])) { ?><h2>Archive</h2>
<?php } ?>

<?php while (have_posts()) : the_post(); ?>
<div class="post">
<h3><?php edit_post_link('[e]','',' '); ?><a href="<?php the_permalink() ?>" title="<?php the_title(); ?>"><?php the_title(); ?></a></h3>
<p class="timestamp"><?php the_time('j F, Y (H:i)') ?> | <?php the_category(', ') ?> | By: <?php the_author(); ?></p>

<div class="contenttext">
<?php the_excerpt(); ?>
</div>

<p class="postmeta"><?php the_tags('Tags: ', ', ', ' | '); ?>
<?php comments_popup_link( 'No comments', '1 comment', '% comments', '', ''); ?></p>
</div>

<?php endwhile; ?>

<div id="postnav">
<p><?php next_posts_link('&laquo; Older entries') ?></p>
<p class="right"><?php previous_posts_link('Newer entries &raquo;') ?></p>
</div>

<?php else : ?>
<h2>Error 404 - Page not found!</h2>
<p>The page or entry that you tried to access could not be found. It may have been moved or deleted. Use the navigation menus or the search box to find what you are looking for!</p>
<?php endif; ?>

</div>

<?php get_sidebar(); ?>
<?php get_footer(); ?>
