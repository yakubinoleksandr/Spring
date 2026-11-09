
INSERT INTO vacancies (
    title,
    description,
    requirements,
    salary,
    employment_type
)
SELECT
    'Java Senior Developer',
    'Розробка мікросервісів',
    'Java 17, Spring Boot, Docker',
    3500.00,
    'FULL_TIME'
WHERE NOT EXISTS (
    SELECT 1 FROM vacancies
    WHERE title = 'Java Senior Developer'
);


INSERT INTO vacancies (
    title,
    description,
    requirements,
    salary,
    employment_type
)
SELECT
    'Frontend React Engineer',
    'Створення UI для платформи',
    'React, TypeScript, CSS',
    2200.00,
    'REMOTE'
WHERE NOT EXISTS (
    SELECT 1 FROM vacancies
    WHERE title = 'Frontend React Engineer'
);


INSERT INTO vacancies (
    title,
    description,
    requirements,
    salary,
    employment_type
)
SELECT
    'QA Automation',
    'Автоматизація тестування API',
    'Java, REST Assured, JUnit',
    NULL,
    'PART_TIME'
WHERE NOT EXISTS (
    SELECT 1 FROM vacancies
    WHERE title = 'QA Automation'
);


INSERT INTO vacancies (
    title,
    description,
    requirements,
    salary,
    employment_type
)
SELECT
    'DevOps Consultant',
    'Налаштування CI/CD пайплайнів',
    'Kubernetes, GitLab CI, Terraform',
    1500.00,
    'PROJECT'
WHERE NOT EXISTS (
    SELECT 1 FROM vacancies
    WHERE title = 'DevOps Consultant'
);



INSERT INTO candidates (full_name, email, skills)
VALUES
    (
        'Олена Коваль',
        'olena.koval@example.com',
        'Java, Spring Boot, PostgreSQL'
    ),
    (
        'Андрій Мельник',
        'andrii.melnyk@example.com',
        'React, TypeScript, CSS'
    ),
    (
        'Ірина Шевченко',
        'iryna.shevchenko@example.com',
        'QA, JUnit, REST API'
    )
ON CONFLICT (email) DO NOTHING;