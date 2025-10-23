document.addEventListener('DOMContentLoaded', () => {

    // --- 1. Existing Theme Switcher Logic ---
    const themeSelector = document.getElementById('theme-selector');
    const themeLink = document.createElement('link');
    themeLink.rel = 'stylesheet';
    document.head.appendChild(themeLink);

    const setTheme = (theme) => {
        if (theme && theme !== 'default') {
            // Updated path assuming themes are in /css/themes/
            themeLink.href = `/css/themes/theme-${theme}.css`;
        } else {
            themeLink.href = '';
        }
        localStorage.setItem('theme', theme || 'default');
    };

    const loadTheme = () => {
        const selectedTheme = localStorage.getItem('theme') || 'default';
        setTheme(selectedTheme);
        if (themeSelector) {
            themeSelector.value = selectedTheme;
        }
    };

    if (themeSelector) {
        themeSelector.addEventListener('change', () => {
            setTheme(themeSelector.value);
        });
    }

    loadTheme();

    // --- 2. New Custom UI Functionality ---
    // We call a new function to initialize the cursor and context menu
    initCustomUI();
});

function initCustomUI() {

    // --- 3. Inject Custom CSS for Cursor and Menu ---
    const styles = `
        /* Custom Cursor Styles */
        body {
            cursor: none; /* Hide the default system cursor */
        }

        #custom-cursor-follower {
            display: none; /* Not used in this implementation */
        }

        #custom-cursor-dot {
            position: fixed;
            left: 0;
            top: 0;
            pointer-events: none;
            z-index: 99999;
            width: 32px; /* Adjust cursor size */
            height: 32px;
            background-color: transparent;
            border: 2px solid var(--primary-accent-color, #1a73e8); /* Border for hover effect */
            border-radius: 50%;
            transition: none;
        }

        /* Hover Effect: Change border color and scale */
        a:hover ~ #custom-cursor-dot,
        button:hover ~ #custom-cursor-dot,
        .btn:hover ~ #custom-cursor-dot,
        .nav-link:hover ~ #custom-cursor-dot,
        select:hover ~ #custom-cursor-dot,
        textarea:hover ~ #custom-cursor-dot,
        input[type="text"]:hover ~ #custom-cursor-dot,
        input[type="email"]:hover ~ #custom-cursor-dot,
        input[type="password"]:hover ~ #custom-cursor-dot,
        input[type="tel"]:hover ~ #custom-cursor-dot {
            width: 40px; /* Slightly increase size on hover */
            height: 40px;
            border-color: var(--primary-accent-color, #1a73e8); /* Keep border color */
            background-color: transparent; /* Ensure no fill */
        }

        /* Custom Context Menu Styles */
        #custom-context-menu {
            position: fixed;
            display: none;
            background: var(--primary-element-bg, #1e1e2f);
            border: 1px solid var(--input-border-color, #404258);
            border-radius: 8px;
            box-shadow: var(--box-shadow, 0 4px 25px rgba(0, 0, 0, 0.3));
            z-index: 10000;
            min-width: 200px;
            overflow: hidden;
            padding: 5px 0;
        }

        .context-menu-item {
            padding: 10px 15px;
            color: var(--main-text-color, #ffffff);
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 10px;
            transition: background-color 0.2s ease;
            font-size: 0.9rem;
        }

        .context-menu-item i.fas {
            width: 16px;
            text-align: center;
            color: var(--sidebar-text-color, #c0c5cc);
        }

        .context-menu-item:hover {
            background-color: var(--primary-accent-color, #1a73e8);
            color: #fff;
        }

        .context-menu-item:hover i.fas {
            color: #fff;
        }

        .context-menu-separator {
            height: 1px;
            background-color: var(--input-border-color, #404258);
            margin: 5px 0;
        }
    `;

    const styleSheet = document.createElement("style");
    styleSheet.type = "text/css";
    styleSheet.innerText = styles;
    document.head.appendChild(styleSheet);

    // --- 4. Custom Cursor Logic ---
    const cursorDot = document.createElement('div');
    cursorDot.id = 'custom-cursor-dot';
    document.body.appendChild(cursorDot);

    document.addEventListener('mousemove', (e) => {
        // Update dot position instantly
        cursorDot.style.left = `${e.clientX}px`;
        cursorDot.style.top = `${e.clientY}px`;
    });

    // --- 5. Custom Context Menu Logic ---
    const contextMenu = document.createElement('div');
    contextMenu.id = 'custom-context-menu';

    // Check current page to dynamically show/hide menu items
    const isProfilePage = window.location.pathname.includes('/profile');
    const isDashboardPage = window.location.pathname.includes('/dashboard');

    // Use Font Awesome icons for the context menu
    contextMenu.innerHTML = `
        <div class="context-menu-item" data-action="back">
            <i class="fas fa-arrow-left"></i> Go Back
        </div>
        <div class="context-menu-item" data-action="reload">
            <i class="fas fa-redo"></i> Reload Page
        </div>
        <div class="context-menu-separator"></div>
        ${!isDashboardPage ? `
        <div class="context-menu-item" data-action="dashboard">
            <i class="fas fa-tachometer-alt"></i> Go to Dashboard
        </div>` : ''}
        ${!isProfilePage ? `
        <div class="context-menu-item" data-action="profile">
            <i class="fas fa-user-circle"></i> View Profile
        </div>` : ''}
        <div class="context-menu-item" data-action="logout">
            <i class="fas fa-sign-out-alt"></i> Logout
        </div>
    `;
    document.body.appendChild(contextMenu);

    // Listen for the right-click event
    document.addEventListener('contextmenu', (e) => {
        e.preventDefault(); // Stop the default browser menu

        // --- Calculate menu position ---
        const { clientX: mouseX, clientY: mouseY } = e;
        const { innerWidth: winWidth, innerHeight: winHeight } = window;
        const { offsetWidth: menuWidth, offsetHeight: menuHeight } = contextMenu;

        const x = mouseX + menuWidth > winWidth ? winWidth - menuWidth - 10 : mouseX;
        const y = mouseY + menuHeight > winHeight ? winHeight - menuHeight - 10 : mouseY;

        contextMenu.style.left = `${x}px`;
        contextMenu.style.top = `${y}px`;
        contextMenu.style.display = 'block';
    });

    // Hide the menu on any left-click
    document.addEventListener('click', () => {
        contextMenu.style.display = 'none';
    });

    // Handle clicks on menu items
    contextMenu.addEventListener('click', (e) => {
        const item = e.target.closest('.context-menu-item');
        if (item) {
            const action = item.dataset.action;

            // Perform action based on data-action attribute
            switch (action) {
                case 'back':
                    window.history.back();
                    break;
                case 'reload':
                    window.location.reload();
                    break;
                case 'profile':
                    const profileLink = document.querySelector('.sidebar-footer a[href*="profile"]');
                    if (profileLink) {
                        window.location.href = profileLink.href;
                    }
                    break;
                case 'dashboard':
                    const dashboardLink = document.querySelector('.sidebar-nav a[href*="dashboard"]');
                    if (dashboardLink) {
                        window.location.href = dashboardLink.href;
                    }
                    break;
                case 'logout':
                    const logoutLink = document.querySelector('.sidebar-footer a[href*="logout"]');
                    if (logoutLink) {
                        window.location.href = logoutLink.href;
                    }
                    break;
            }
        }
    });
}
