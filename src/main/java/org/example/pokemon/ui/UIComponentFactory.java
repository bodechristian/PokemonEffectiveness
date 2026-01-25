package org.example.pokemon.ui;

import org.example.pokemon.model.PokemonType;
import org.example.pokemon.ui.util.ColorUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class UIComponentFactory {
    
    public JLabel createSelectedTypeLabel(PokemonType type) {
        JLabel label = new JLabel(type.getDisplayName());
        label.setOpaque(true);
        label.setBackground(type.getColor());
        label.setForeground(ColorUtils.getContrastingTextColor(type.getColor()));
        label.setFont(new Font("Arial", Font.BOLD, 10));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(70, 28));
        label.setMinimumSize(new Dimension(70, 28));
        label.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(50, 50, 50), 3),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return label;
    }
    
    public JLabel createEmptyTypeLabel() {
        JLabel label = new JLabel("None");
        label.setOpaque(false);
        label.setForeground(Color.GRAY);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(70, 28));
        label.setMinimumSize(new Dimension(70, 28));
        label.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 2),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return label;
    }
    
    public JPanel createSelectedTypeDisplayPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        panel.setBackground(Color.WHITE);
        return panel;
    }
    
    public JPanel createSelectorContainer(JPanel displayPanel, TypeSelectorPanel selectorPanel) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        container.add(displayPanel);
        container.add(selectorPanel);
        return container;
    }
    
    public Component createHorizontalSpacing(int width) {
        return Box.createHorizontalStrut(width);
    }
    
    public JSeparator createHorizontalSeparator() {
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        return separator;
    }
    
    public JPanel createVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        return panel;
    }
    
    public Component createVerticalSpacing(int height) {
        return Box.createVerticalStrut(height);
    }
    
    public JScrollPane createResultsScrollPane(Component viewportView) {
        JScrollPane scrollPane = new JScrollPane(viewportView);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }
}
