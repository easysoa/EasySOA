<?php get_header(); ?>

<div id="left">
<?php if (have_posts()) : ?>
<h2>Search results</h2>
<p>Your search for <span class="highlight">"<?php echo wp_specialchars($s, 1); ?>"</span> returned the following matches:</p>
<?php while (have_posts()) : the_post(); ?>
<div class="post">
<h2><?php edit_post_link('[e]','',' '); ?><a href="<?php the_permalink() ?>" rel="bookmark" title="<?php the_title(); ?>"><?php the_title(); ?></a></h2>
<p class="timestamp"><?php the_time('j F, Y (H:i)') ?> | <?php the_category(', ') ?> | <?php comments_popup_link( 'No comments', '1 comment', '% comments', '', ''); ?></p>
<div class="contenttext">
<?php the_excerpt() ?>
</div>
</div>

<?php endwhile; ?>

<div id="postnav">
<p><?php next_posts_link('&laquo; Older entries') ?></p>
<p class="right"><?php previous_posts_link('Newer entries &raquo;') ?></p>
</div>

<?php else : ?>
<h2>No matches found!</h2>
<p>Your search for "<?php echo wp_specialchars($s, 1); ?>" gave no matches. Please try a different word, or use the navigation menus to search the site.</p>
<?php endif; ?>

</div>

<?php get_sidebar(); ?>
<?php get_footer(); ?>
